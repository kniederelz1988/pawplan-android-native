package de.kniederelz.pawplan.appointments.repositories.ratings.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingStatistics
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreAppointmentRatingRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AppointmentRatingRepository {
    companion object {
        const val COLLECTION = "appointmentsRating"
    }

    override suspend fun updateRating(rating: AppointmentRating) : Result<String> {
        if (rating.id.isBlank()) {
            return Result.failure(IllegalArgumentException("Rating id cannot be blank"))
        }

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
                .addSnapshotListener { snapshot, error ->
                    if (error != null || snapshot == null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val rating = snapshot.documents.firstOrNull()?.let {
                        it.toObject(FirebaseAppointmentRatingDto::class.java)
                            ?.toDomain(it.id)
                    }

                    trySend(rating)
                }

            awaitClose {
                registration.remove()
            }
        }
    override fun observeRatings(appointmentIds: List<String>): Flow<Map<String, AppointmentRating>> {
        return combine(
            appointmentIds.map { observeRating(it) }
        ) { ratings ->
            ratings.filterNotNull().associateBy { it.id }
        }
    }

    override fun observeDogRatings(dogId: String) = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .whereEqualTo("dogId", dogId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }

                val ratings = snapshot.documents.mapNotNull {
                    it.toObject(FirebaseAppointmentRatingDto::class.java)
                        ?.toDomain(it.id)
                }

                trySend(ratings)
            }

        awaitClose {
            registration.remove()
        }
    }

    override fun observeDogStatistics(dogId: String) = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .whereEqualTo("dogId", dogId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot.isEmpty) {
                    trySend(null)
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

                trySend(AppointmentRatingStatistics(dogId, average, count))
            }

        awaitClose {
            registration.remove()
        }
    }
    override fun observeDogStatistics(dogIds: List<String>): Flow<Map<String, AppointmentRatingStatistics>> {
        return combine(
            dogIds.map { observeDogStatistics(it) }
        ) { statistics ->
            statistics.filterNotNull().associateBy { it.id }
        }
    }
}