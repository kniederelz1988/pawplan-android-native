package de.kniederelz.pawplan.appointments.repositories.base.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.core.RepositorySubscription
import de.kniederelz.pawplan.core.utils.TimestampUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FirestoreVolunteerAppointmentSubscriptionImpl(
    private val firestore: FirebaseFirestore
) : RepositorySubscription<Appointment>() {
    private val _listeners = mutableMapOf<String, ListenerRegistration>()

    private val _values = MutableStateFlow<Map<String, Appointment>>(emptyMap())
    override val values: Flow<Map<String, Appointment>> = _values

    override fun registerListener(id: String): FirestoreVolunteerAppointmentSubscriptionImpl {
        if (_listeners.containsKey(id))
            return this

        _listeners[id] = firestore
            .collection(FirestoreAppointmentRepositoryImpl.COLLECTION)
            .whereEqualTo("volunteerId", id)
            .whereGreaterThanOrEqualTo("date", TimestampUtils.startOfDay())
            .orderBy("date", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null)
                    return@addSnapshotListener

                val appointments = snapshots.documents.mapNotNull {
                    it.toObject(FirebaseAppointmentDto::class.java)
                        ?.toDomain(it.id)
                }

                _values.update {
                    it + appointments.associateBy { it.id }
                }
            }

        return this
    }
    override fun deregisterListener(id: String): FirestoreVolunteerAppointmentSubscriptionImpl {
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

