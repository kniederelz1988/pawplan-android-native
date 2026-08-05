package de.kniederelz.pawplan.appointments.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointments.repositories.base.data.FirebaseAppointmentDto
import de.kniederelz.pawplan.appointments.repositories.base.data.FirestoreAppointmentRepositoryImpl
import de.kniederelz.pawplan.appointments.repositories.base.data.toDomain
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentData
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusSubscription
import de.kniederelz.pawplan.dogs.data.FirebaseDogDto
import de.kniederelz.pawplan.dogs.data.FirebaseDogRepositoryImpl
import de.kniederelz.pawplan.dogs.data.sources.extensions.toDomain
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogSubscription
import kotlinx.coroutines.tasks.await

class FirestoreAppointmentDataSource (
    private val firestore: FirebaseFirestore,
    private val volunteerId: String,
    private val statusSubscription: AppointmentStatusSubscription,
    private val dogSubscription: DogSubscription
) : PagingSource<DocumentSnapshot, Appointment>() {
    private suspend fun getAppointmentDocuments(limit: Long, lastDocument: DocumentSnapshot?)
        : Collection<DocumentSnapshot> {
        try {
            var query = firestore
                .collection(FirestoreAppointmentRepositoryImpl.COLLECTION)
                .whereEqualTo("volunteerId", volunteerId)
                //.whereGreaterThanOrEqualTo("date", TimestampUtils.startOfDay())
                .orderBy("date")
                .limit(limit)

            lastDocument?.let {
                query = query.startAfter(it)
            }

            val snapshot = query.get()
                .await()

            return snapshot.documents.mapNotNull { it }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>)
        : LoadResult<DocumentSnapshot, Appointment> {
        return try {
            val appointmentDocuments = getAppointmentDocuments(params.loadSize.toLong(), params.key)
            val appointments = appointmentDocuments.mapNotNull {
                val dto = it.toObject(FirebaseAppointmentDto::class.java)
                dto?.toDomain(it.id)
            }

            statusSubscription.registerBatchListener( appointments.map { it.id }.distinct())
            dogSubscription.registerBatchListener( appointments.map { it.dogId }.distinct() )

            LoadResult.Page(
                data = appointments,
                prevKey = null,
                nextKey = appointmentDocuments.lastOrNull()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Appointment>): DocumentSnapshot? = null
}