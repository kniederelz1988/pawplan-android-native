package de.kniederelz.pawplan.appointments.repositories.base.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.NonCancellable
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

    override fun createSubscription(): RepositorySubscription<Appointment> {
        return FirestoreAppointmentSubscriptionImpl(firestore)
    }
    override fun createVolunteerSubscription(): RepositorySubscription<Appointment> {
        return FirestoreVolunteerAppointmentSubscriptionImpl(firestore)
    }
}