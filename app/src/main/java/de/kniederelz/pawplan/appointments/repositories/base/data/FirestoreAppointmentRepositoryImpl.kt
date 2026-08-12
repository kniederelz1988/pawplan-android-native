package de.kniederelz.pawplan.appointments.repositories.base.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.core.utils.TimestampUtils
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreAppointmentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AppointmentRepository {
    companion object {
        const val COLLECTION = "appointments2"
    }

    override suspend fun createAppointment(appointment: Appointment) : Result<String> {
        return withContext(NonCancellable) {
            try {
                val result = firestore
                    .collection(COLLECTION)
                    .add(appointment.toDto())
                    .await()

                Result.success(result.id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override suspend fun updateAppointment(appointment: Appointment) : Result<Unit> {
        return withContext(NonCancellable) {
            try {
                firestore
                    .collection(COLLECTION)
                    .document(appointment.id)
                    .set(appointment.toDto())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun observeAppointment(appointmentId: String) = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .document(appointmentId)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null){
                    close(error)
                    return@addSnapshotListener
                }

                if (!snapshots.exists())
                {
                    trySend(null)
                    return@addSnapshotListener
                }

                val appointment = snapshots.toObject(FirebaseAppointmentDto::class.java)
                    ?.toDomain(appointmentId)
                trySend(appointment)
            }

        awaitClose {
            registration.remove()
        }
    }
    override fun observeAppointments(appointmentIds: List<String>): Flow<Map<String, Appointment>> {
        return combine(
            appointmentIds.map { observeAppointment(it) }
        ) { appointments ->
            appointments.filterNotNull().associateBy { it.id }
        }
    }

    override fun observeAllVolunteerAppointments(volunteerId: String) = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .whereEqualTo("volunteerId", volunteerId)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshots.isEmpty)
                {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val appointments = snapshots.documents.mapNotNull {
                    it.toObject(FirebaseAppointmentDto::class.java)
                        ?.toDomain(it.id)
                }

                trySend(appointments)
            }

        awaitClose {
            registration.remove()
        }
    }

    override fun observeUpcomingVolunteerAppointments(volunteerId: String) = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .whereEqualTo("volunteerId", volunteerId)
            .whereGreaterThanOrEqualTo("date", TimestampUtils.startOfDay())
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshots.isEmpty)
                {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val appointments = snapshots.documents.mapNotNull {
                    it.toObject(FirebaseAppointmentDto::class.java)
                        ?.toDomain(it.id)
                }

                trySend(appointments)
            }

        awaitClose {
            registration.remove()
        }
    }
}