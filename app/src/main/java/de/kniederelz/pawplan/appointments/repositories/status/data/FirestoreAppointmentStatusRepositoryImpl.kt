package de.kniederelz.pawplan.appointments.repositories.status.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusSubscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FirestoreAppointmentStatusRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): AppointmentStatusRepository {
    companion object {
        class AppointmentStatusSubscriptionImpl(
            private val firestore: FirebaseFirestore
        )
            : AppointmentStatusSubscription {
            private val _listeners = mutableMapOf<String, ListenerRegistration>()

            private val _values = MutableStateFlow(emptyMap<String, AppointmentStatus>())
            override val values: Flow<Map<String, AppointmentStatus>> = _values

            override fun registerStatusListener(appointmentId: String) {
                if (_listeners.containsKey(appointmentId))
                    return

                _listeners[appointmentId] = firestore
                    .collection("appointmentsStatus")
                    .document(appointmentId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null)
                            return@addSnapshotListener

                        val status = snapshot.toObject(FirebaseAppointmentStatusDto::class.java)
                            ?.toDomain(snapshot.id) ?: return@addSnapshotListener

                        _values.update {
                            it + (appointmentId to status)
                        }
                    }
            }
            override fun deregisterStatusListener(appointmentId: String) {
                if (!_listeners.containsKey(appointmentId))
                    return

                _listeners[appointmentId]?.remove()
                _listeners.remove(appointmentId)
            }

            override fun registerBatchListener(appointmentIds: List<String>) {
                appointmentIds.forEach { registerStatusListener(it ) }
            }
            override fun deregisterBatchListener(appointmentIds: List<String>) {
                appointmentIds.forEach { deregisterStatusListener(it) }
            }

            override fun close() {
                _listeners.values.forEach {
                    it.remove()
                }
                _listeners.clear()
            }
        }
    }

    override suspend fun createStatus(status: AppointmentStatus) {
        TODO("Not yet implemented")
    }
    override suspend fun updateStatus(status: AppointmentStatus) {
        TODO("Not yet implemented")
    }

    override fun createSubscription(): AppointmentStatusSubscription {
        return AppointmentStatusSubscriptionImpl(firestore)
    }
}