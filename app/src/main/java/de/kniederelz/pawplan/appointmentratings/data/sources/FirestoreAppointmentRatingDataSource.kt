package de.kniederelz.pawplan.appointmentratings.data.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointmentratings.data.FirebaseAppointmentRatingDto
import de.kniederelz.pawplan.appointmentratings.data.FirestoreAppointmentRatingRepositoryImpl
import de.kniederelz.pawplan.appointmentratings.data.extensions.toDomain
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRating
import kotlinx.coroutines.tasks.await

class FirestoreAppointmentRatingDataSource(
    private val firestore: FirebaseFirestore,
    private val dogId: String
) : PagingSource<DocumentSnapshot, AppointmentRating>() {
    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, AppointmentRating> {
        return try {
            var query = firestore
                .collection(FirestoreAppointmentRatingRepositoryImpl.COLLECTION)
                .whereEqualTo("dogId", dogId)
                .limit(params.loadSize.toLong())

            params.key?.let { lastDocument ->
                query = query.startAfter(lastDocument)
            }

            val snapshot = query.get()
                .await()

            val ratings = snapshot.documents.mapNotNull { document ->
                val dto = document.toObject(FirebaseAppointmentRatingDto::class.java)
                dto?.toDomain(document.id)
            }

            LoadResult.Page(
                data = ratings,
                prevKey = null,
                nextKey = snapshot.documents.lastOrNull()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, AppointmentRating>): DocumentSnapshot? = null
}

