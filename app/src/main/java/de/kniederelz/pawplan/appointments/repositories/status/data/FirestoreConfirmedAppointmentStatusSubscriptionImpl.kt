package de.kniederelz.pawplan.appointments.repositories.status.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FirestoreConfirmedAppointmentStatusSubscriptionImpl(
    private val firestore: FirebaseFirestore,
    private val status: Collection<AppointmentStatusType>
) : RepositorySubscription<AppointmentStatus>() {
    private val _listeners = mutableMapOf<String, ListenerRegistration>()

    private val _values = MutableStateFlow(emptyMap<String, AppointmentStatus>())
    override val values: Flow<Map<String, AppointmentStatus>> = _values

    override fun registerListener(id: String): FirestoreConfirmedAppointmentStatusSubscriptionImpl {
        if (_listeners.containsKey(id))
            return this

        val statusFilter = status.map { AppointmentStatusType.entries.indexOf(it) }
        _listeners[id] = firestore
            .collection("appointmentsStatus")
            .whereEqualTo("volunteerId", id)
            .whereIn("status", statusFilter)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null)
                    return@addSnapshotListener

                val status = snapshot.documents.mapNotNull {
                    it.toObject(FirebaseAppointmentStatusDto::class.java)
                        ?.toDomain(it.id) ?: return@addSnapshotListener
                }

                _values.update {
                    status.associateBy { status -> status.id }
                }
            }

        return this
    }
    override fun deregisterListener(id: String): FirestoreConfirmedAppointmentStatusSubscriptionImpl {
        if (!_listeners.containsKey(id))
            return this

        _listeners[id]?.remove()
        _listeners.remove(id)

        return this
    }

    override fun close() {
        _listeners.values.forEach {
            it.remove()
        }
        _listeners.clear()
    }
}