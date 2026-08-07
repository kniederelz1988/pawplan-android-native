package de.kniederelz.pawplan.tracking.repositories.session.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.core.RepositorySubscription
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionSubscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.collections.emptyMap
import kotlin.collections.plus

class FirestoreWalkingTrackerSessionSubscriptionImpl(
    private val firestore: FirebaseFirestore
) : WalkingTrackerSessionSubscription() {
    private val _listeners = mutableMapOf<String, ListenerRegistration>()

    private val _values = MutableStateFlow<Map<String, WalkingTrackerSession>>(emptyMap())
    override val values = _values

    override fun registerListener(id: String): RepositorySubscription<WalkingTrackerSession> {
        if (_listeners.containsKey(id))
            return this

        _listeners[id] = firestore
            .collection(FirestoreWalkingTrackerSessionRepositoryImpl.COLLECTION)
            .document(id)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null)
                    return@addSnapshotListener

                val session = snapshots.toObject(FirestoreWalkingTrackerSessionDto::class.java)
                    ?.toDomain(id)

                session?.let { session ->
                    _values.update {
                        it + (session.id to session)
                    }
                }
            }

        return this
    }
    override fun deregisterListener(id: String): RepositorySubscription<WalkingTrackerSession> {
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