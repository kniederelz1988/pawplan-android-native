package de.kniederelz.pawplan.appointments.repositories.base.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FirestoreAppointmentSubscriptionImpl(
    private val firestore: FirebaseFirestore
) : RepositorySubscription<Appointment>() {
    private val _listeners = mutableMapOf<String, ListenerRegistration>()

    private val _values = MutableStateFlow<Map<String, Appointment>>(emptyMap())
    override val values: Flow<Map<String, Appointment>> = _values

    override fun registerListener(id: String): FirestoreAppointmentSubscriptionImpl {
        if (_listeners.containsKey(id))
            return this

        _listeners[id] = firestore
            .collection(FirestoreAppointmentRepositoryImpl.COLLECTION)
            .document(id)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null)
                    return@addSnapshotListener

                val appointment = snapshots.toObject(FirebaseAppointmentDto::class.java)
                        ?.toDomain(id)

                appointment?.let {
                    _values.update {
                        it + (id to appointment)
                    }
                }
            }

        return this
    }
    override fun deregisterListener(id: String): FirestoreAppointmentSubscriptionImpl {
        if (!_listeners.containsKey(id))
            return this

        return this
    }

    override fun close() {
        _listeners.values.forEach {
            it.remove()
        }
        _listeners.clear()
    }
}