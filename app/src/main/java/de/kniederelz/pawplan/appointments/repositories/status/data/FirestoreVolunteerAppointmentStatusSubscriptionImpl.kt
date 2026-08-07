package de.kniederelz.pawplan.appointments.repositories.status.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FirestoreVolunteerAppointmentStatusSubscriptionImpl(
    private val firestore: FirebaseFirestore
) : RepositorySubscription<AppointmentStatus>() {
    private val _listeners = mutableMapOf<String, ListenerRegistration>()

    private val _values = MutableStateFlow(emptyMap<String, AppointmentStatus>())
    override val values: Flow<Map<String, AppointmentStatus>> = _values

    override fun registerListener(id: String): FirestoreVolunteerAppointmentStatusSubscriptionImpl {
        if (_listeners.containsKey(id))
            return this

        _listeners[id] = firestore
            .collection("appointmentsStatus")
            .whereEqualTo("volunteerId", id)
            .whereEqualTo("status", AppointmentStatusType.entries.indexOf(AppointmentStatusType.CONFIRMED))
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null)
                    return@addSnapshotListener

                Log.d("FirestoreAppointmentStatusSubscriptionImpl", "Snapshot: ${snapshot.size()}")
                val status = snapshot.documents.mapNotNull {
                    it.toObject(FirebaseAppointmentStatusDto::class.java)
                        ?.toDomain(it.id) ?: return@addSnapshotListener
                }

                _values.update {
                    it + status.associateBy { status -> status.id }
                }
            }

        return this
    }
    override fun deregisterListener(id: String): FirestoreVolunteerAppointmentStatusSubscriptionImpl {
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