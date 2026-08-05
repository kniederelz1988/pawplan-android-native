package de.kniederelz.pawplan.appointments.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import de.kniederelz.pawplan.appointments.repositories.base.data.FirebaseAppointmentDto
import de.kniederelz.pawplan.appointments.repositories.base.data.FirestoreAppointmentRepositoryImpl
import de.kniederelz.pawplan.appointments.repositories.base.data.toDomain
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentData
import de.kniederelz.pawplan.appointments.repositories.status.data.FirebaseAppointmentStatusDto
import de.kniederelz.pawplan.appointments.repositories.status.data.FirestoreAppointmentStatusRepositoryImpl
import de.kniederelz.pawplan.appointments.repositories.status.data.toDomain
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.utils.TimestampUtils
import de.kniederelz.pawplan.dogs.data.FirebaseDogDto
import de.kniederelz.pawplan.dogs.data.FirebaseDogRepositoryImpl
import de.kniederelz.pawplan.dogs.data.sources.extensions.toDomain
import de.kniederelz.pawplan.dogs.domain.Dog
import kotlinx.coroutines.tasks.await

class FirestoreAppointmentDataSource (
    private val firestore: FirebaseFirestore,
    private val volunteerId: String,
    private val statusTypes: List<AppointmentStatusType>
) : PagingSource<DocumentSnapshot, AppointmentData>() {
    private suspend fun getAppointmentDocuments(limit: Long, lastDocument: DocumentSnapshot?)
        : Collection<DocumentSnapshot> {
        try {
            var query = firestore
                .collection(FirestoreAppointmentRepositoryImpl.COLLECTION)
                .whereEqualTo("volunteerId", volunteerId)
                .whereGreaterThanOrEqualTo("date", TimestampUtils.startOfDay())
                .orderBy("date", Query.Direction.ASCENDING)

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
    private suspend fun getAppointmentStatusDocuments(appointmentIds: List<String>)
        : Collection<DocumentSnapshot> {
        if(appointmentIds.isEmpty())
            return emptyList()

        try {
            val snapshot = firestore
                .collection(FirestoreAppointmentStatusRepositoryImpl.COLLECTION)
                .whereIn("appointmentId", appointmentIds)
                .get()
                .await()

            return snapshot.documents.mapNotNull { it }
        } catch (e: Exception) {
            throw e
        }
    }

    private suspend fun getDogDocuments(dogIds: List<String>)
        : Collection<DocumentSnapshot> {
        if (dogIds.isEmpty())
            return emptyList()

        try {
            val query = firestore
                .collection(FirebaseDogRepositoryImpl.COLLECTION)
                .whereIn(FieldPath.documentId(), dogIds)

            val snapshot = query.get()
                .await()

            return snapshot.documents.mapNotNull { it }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>)
        : LoadResult<DocumentSnapshot, AppointmentData> {
        return try {
            val appointmentDocuments = getAppointmentDocuments(params.loadSize.toLong(), params.key)
            val appointments = appointmentDocuments.mapNotNull { document ->
                document.toObject(FirebaseAppointmentDto::class.java)
                    ?.toDomain(document.id)
            }

            Log.d("FirestoreAppointmentDataSource", "Loaded appointments: $appointments")

            val appointmentStatusDocuments = getAppointmentStatusDocuments(appointments.map { it.id }.distinct())
            val appointmentStatuses = appointmentStatusDocuments.mapNotNull { document ->
                document.toObject(FirebaseAppointmentStatusDto::class.java)
                    ?.toDomain(document.id)
                    ?.let { it.id to it }
            }.toMap()

            Log.d("FirestoreAppointmentDataSource", "Loaded statuses: $appointmentStatuses")

            val dogDocuments = getDogDocuments(appointments.map { it.dogId }.distinct())
            val dogs = dogDocuments.mapNotNull { document ->
                document.toObject(FirebaseDogDto::class.java)
                    ?.toDomain(document.id)
                    ?.let { it.id to it }
            }.toMap()

            Log.d("FirestoreAppointmentDataSource", "Loaded dogs: $dogs")

            appointments.forEach {
                Log.d("FirestoreAppointmentDataSource",
                    "Status: ${appointmentStatuses.containsKey(it.id)}, " +
                            "Dog: ${dogs.containsKey(it.dogId)}"
                )
            }

            val data = appointments.filter {
                appointmentStatuses.containsKey(it.id) && dogs.containsKey(it.dogId)
            }.map {
                AppointmentData(
                    it.id,
                    it,
                    appointmentStatuses[it.id] ?: AppointmentStatus.EMPTY,
                    dogs[it.dogId] ?: Dog.EMPTY
                )
            }

            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = appointmentDocuments.lastOrNull()
            )
        } catch (e: Exception) {
            Log.e("FirestoreAppointmentDataSource", "Failed to load appointments", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, AppointmentData>): DocumentSnapshot? = null
}