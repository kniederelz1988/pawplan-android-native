package de.kniederelz.pawplan.appointments.repositories.ratings.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingStatistics
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.appointments.sources.factories.FirestoreAppointmentRatingDataSourceFactory
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreAppointmentRatingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sourceFactory: FirestoreAppointmentRatingDataSourceFactory
) : AppointmentRatingRepository {
    companion object {
        const val COLLECTION = "appointmentsRating"
    }

    override suspend fun createRating(rating: AppointmentRating): Result<String> {
        return updateRating(rating)
    }
    override suspend fun updateRating(rating: AppointmentRating) : Result<String> {
        return withContext(NonCancellable) {
            try {
                firestore
                    .collection(COLLECTION)
                    .document(rating.id)
                    .set(rating.toDto())
                    .await()

                Result.success(rating.id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun observeRating(appointmentId: String): Flow<AppointmentRating?> =
        callbackFlow {
            val registration = firestore
                .collection(COLLECTION)
                .whereEqualTo("appointmentId", appointmentId)
                .limit(1)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val rating = snapshot
                        ?.documents
                        ?.firstOrNull()
                        ?.let {
                            it.toObject(FirebaseAppointmentRatingDto::class.java)
                                ?.toDomain(it.id)
                        }

                    trySend(rating)
                }

            awaitClose {
                registration.remove()
            }
        }
    override fun observeRatings(dogId: String): Flow<PagingData<AppointmentRating>> =
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                sourceFactory.createPagingSource(dogId)
            }
        ).flow

    private val _dogStatistics = MutableStateFlow(emptyMap<String, AppointmentRatingStatistics>())
    override val dogStatistics = _dogStatistics.asStateFlow()

    private val _dogStatisticListeners = mutableMapOf<String, ListenerRegistration>()
    override fun requestDogStatistics(dogId: String) {
        if (_dogStatisticListeners.containsKey(dogId)) {
            return
        }

        _dogStatisticListeners[dogId] = firestore
            .collection(COLLECTION)
            .whereEqualTo("dogId", dogId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val ratings = snapshot.documents.mapNotNull {
                    it.toObject(FirebaseAppointmentRatingDto::class.java)
                }

                val count = ratings.size
                val average =
                    if (count == 0) 0f else {
                        ratings.sumOf { it.rating }.toFloat() / count
                    }

                _dogStatistics.update {
                    it + (dogId to AppointmentRatingStatistics(average, count))
                }
            }
    }

    override fun createSubscription() : RepositorySubscription<AppointmentRating> {
        return FirestoreAppointmentRatingSubscriptionImpl(firestore)
    }
}