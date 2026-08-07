package de.kniederelz.pawplan.appointments.repositories.ratings.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FirestoreAppointmentRatingSubscriptionImpl(
    private val firestore: FirebaseFirestore
) : RepositorySubscription<AppointmentRating>() {
    private val _listeners = mutableMapOf<String, ListenerRegistration>()

    private val _values = MutableStateFlow<Map<String, AppointmentRating>>(emptyMap())
    override val values: Flow<Map<String, AppointmentRating>> = _values

    override fun registerListener(id: String): FirestoreAppointmentRatingSubscriptionImpl {
        if (_listeners.containsKey(id))
            return this

        _listeners[id] = firestore
            .collection(FirestoreAppointmentRatingRepositoryImpl.COLLECTION)
            .document(id)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null)
                    return@addSnapshotListener

                val appointmentRating = snapshots.toObject(FirebaseAppointmentRatingDto::class.java)
                    ?.toDomain(id)

                appointmentRating?.let {
                    _values.update {
                        it + (id to appointmentRating)
                    }
                }
            }

        return this
    }
    override fun deregisterListener(id: String): FirestoreAppointmentRatingSubscriptionImpl {
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