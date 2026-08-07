package de.kniederelz.pawplan.appointments.repositories.status.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FirestoreAppointmentStatusSubscriptionImpl(
    private val firestore: FirebaseFirestore
) : RepositorySubscription<AppointmentStatus>() {
    private val _listeners = mutableMapOf<String, ListenerRegistration>()

    private val _values = MutableStateFlow(emptyMap<String, AppointmentStatus>())
    override val values: Flow<Map<String, AppointmentStatus>> = _values

    override fun registerListener(id: String): FirestoreAppointmentStatusSubscriptionImpl {
        if (_listeners.containsKey(id))
            return this

        _listeners[id] = firestore
            .collection("appointmentsStatus")
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null)
                    return@addSnapshotListener

                val status = snapshot.toObject(FirebaseAppointmentStatusDto::class.java)
                    ?.toDomain(snapshot.id) ?: return@addSnapshotListener

                _values.update {
                    it + (id to status)
                }
            }

        return this
    }
    override fun deregisterListener(id: String): FirestoreAppointmentStatusSubscriptionImpl {
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

