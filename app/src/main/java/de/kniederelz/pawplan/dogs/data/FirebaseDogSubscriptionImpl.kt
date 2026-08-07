package de.kniederelz.pawplan.dogs.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.core.RepositorySubscription
import de.kniederelz.pawplan.dogs.data.sources.extensions.toDomain
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogSubscription
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FirebaseDogSubscriptionImpl(
    private val firestore: FirebaseFirestore
) : RepositorySubscription<Dog>() {
    private val _listeners = mutableMapOf<String, ListenerRegistration>()

    private val _values = MutableStateFlow(emptyMap<String, Dog>())
    override val values = _values

    override fun registerListener(id: String): FirebaseDogSubscriptionImpl {
        if (_listeners.containsKey(id))
            return this

        _listeners[id] = firestore
            .collection(FirebaseDogRepositoryImpl.COLLECTION)
            .document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null)
                    return@addSnapshotListener

                val dog = snapshot.toObject(FirebaseDogDto::class.java)
                    ?.toDomain(snapshot.id) ?: return@addSnapshotListener

                _values.update {
                    it + (id to dog)
                }
            }

        return this
    }
    override fun deregisterListener(id: String): FirebaseDogSubscriptionImpl {
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