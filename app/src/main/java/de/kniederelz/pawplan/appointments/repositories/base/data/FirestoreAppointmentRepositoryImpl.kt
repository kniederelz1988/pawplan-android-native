package de.kniederelz.pawplan.appointments.repositories.base.data

import android.util.Log
import androidx.paging.PagingSource
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.appointments.sources.FirestoreAppointmentDataSource
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentData
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.core.utils.TimestampUtils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreAppointmentRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AppointmentRepository {
    companion object {
        const val COLLECTION = "appointments2"
    }

    override suspend fun createAppointment(appointment: Appointment) : Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION)
                .add(appointment.toDto())
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreAppointmentRepository", "Failed to create appointment", e)
            Result.failure(e)
        }
    }
    override suspend fun updateAppointment(appointment: Appointment) : Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION)
                .document(appointment.id)
                .set(appointment.toDto())
                .await()

            return Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreAppointmentRepository", "Failed to update appointment", e)
            Result.failure(e)
        }
    }

    override fun observeNextAppointment(volunteerId: String): Flow<Appointment?> =
        callbackFlow {
            val registration = firestore
                .collection(COLLECTION)
                .whereGreaterThanOrEqualTo("date", TimestampUtils.startOfDay())
                .orderBy("date")
                .limit(1)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }

                    val appointment = snapshot?.documents?.firstOrNull()?.let {
                        it.toObject(FirebaseAppointmentDto::class.java)
                            ?.toDomain(it.id)
                    }

                    trySend(appointment)
                }

            awaitClose {
                registration.remove()
            }
        }

    override fun getAppointmentDataSource(volunteerId: String): PagingSource<*, AppointmentData> {
        return FirestoreAppointmentDataSource(firestore, volunteerId)
    }
}