package de.kniederelz.pawplan.appointments.repositories.status.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreAppointmentStatusRepositoryImpl(
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

    override fun observeStatus(appointmentId: String): Flow<AppointmentStatus?> {
        TODO("Not yet implemented")
    }
}