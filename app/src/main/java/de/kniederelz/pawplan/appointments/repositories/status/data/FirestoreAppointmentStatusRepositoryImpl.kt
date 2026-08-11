package de.kniederelz.pawplan.appointments.repositories.status.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.NonCancellable
import javax.inject.Inject
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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

    override fun createSubscription(): RepositorySubscription<AppointmentStatus> {
        return FirestoreAppointmentStatusSubscriptionImpl(firestore)
    }
    override fun createSubscriptionFilteredByStatus(status: Collection<AppointmentStatusType>)
        : RepositorySubscription<AppointmentStatus> {
        return FirestoreConfirmedAppointmentStatusSubscriptionImpl(firestore, status)
    }
}