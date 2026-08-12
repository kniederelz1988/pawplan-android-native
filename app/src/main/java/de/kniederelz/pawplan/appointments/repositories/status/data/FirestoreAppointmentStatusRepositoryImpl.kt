package de.kniederelz.pawplan.appointments.repositories.status.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.collections.set

class FirestoreAppointmentStatusRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): AppointmentStatusRepository {
    companion object {
        const val COLLECTION = "appointmentsStatus"
    }

    override suspend fun createStatus(status: AppointmentStatus): Result<String> {
        return withContext(NonCancellable) {
            try {
                firestore
                    .collection(COLLECTION)
                    .document(status.id)
                    .set(status.toDto())
                    .await()

                Result.success(status.id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override suspend fun updateStatus(status: AppointmentStatus): Result<Unit> {
        return withContext(NonCancellable) {
            try {
                firestore
                    .collection(COLLECTION)
                    .document(status.id)
                    .set(status.toDto())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun observeStatus(appointmentId: String) = callbackFlow {
        val registration = firestore
            .collection("appointmentsStatus")
            .document(appointmentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (!snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val status = snapshot.toObject(FirebaseAppointmentStatusDto::class.java)
                    ?.toDomain(snapshot.id) ?: return@addSnapshotListener

                trySend(status)
            }

        awaitClose {
            registration.remove()
        }
    }
    override fun observeVolunteerStatus(volunteerId: String, status: Collection<AppointmentStatusType>) = callbackFlow {
        val statusFilter = status.map { AppointmentStatusType.entries.indexOf(it) }

        val registration = firestore
            .collection("appointmentsStatus")
            .whereEqualTo("volunteerId", volunteerId)
            .whereIn("status", statusFilter)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null)
                {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot.isEmpty)
                {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val status = snapshot.documents.mapNotNull {
                    it.toObject(FirebaseAppointmentStatusDto::class.java)
                        ?.toDomain(it.id) ?: return@addSnapshotListener
                }

                trySend(status)
            }

        awaitClose {
            registration.remove()
        }
    }
}