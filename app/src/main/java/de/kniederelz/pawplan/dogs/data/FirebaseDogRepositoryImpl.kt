package de.kniederelz.pawplan.dogs.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import de.kniederelz.pawplan.dogs.data.sources.FirestoreDogsDataSource
import de.kniederelz.pawplan.dogs.data.sources.extensions.toDomain
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.sources.factory.DogDataSourceFactory
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.dogs.domain.DogSubscription
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class FirebaseDogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sourceFactory: DogDataSourceFactory,
) : DogRepository {
    companion object {
        const val COLLECTION = "dogs"

        class FirebaseDogSubscriptionImpl(
            private val firestore: FirebaseFirestore
        ) : DogSubscription {
            private val _listeners = mutableMapOf<String, ListenerRegistration>()

            private val _values = MutableStateFlow(emptyMap<String, Dog>())
            override val values = _values

            override fun registerStatusListener(dogId: String) {
                if (_listeners.containsKey(dogId))
                    return

                _listeners[dogId] = firestore
                    .collection(COLLECTION)
                    .document(dogId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null || snapshot == null)
                            return@addSnapshotListener

                        val dog = snapshot.toObject(FirebaseDogDto::class.java)
                            ?.toDomain(snapshot.id) ?: return@addSnapshotListener

                        _values.update {
                            it + (dogId to dog)
                        }
                    }
            }
            override fun deregisterStatusListener(dogId: String) {
                if (!_listeners.containsKey(dogId))
                    return
            }

            override fun registerBatchListener(dogIds: List<String>) {
                dogIds.forEach { registerStatusListener(it) }
            }
            override fun deregisterBatchListener(dogIds: List<String>) {
                dogIds.forEach { deregisterStatusListener(it) }
            }

            override fun close() {
                _listeners.values.forEach {
                    it.remove()
                }
                _listeners.clear()
            }
        }
    }

    override fun observeDog(dogId: String): Flow<Dog?> = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .document(dogId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val dto = snapshot.toObject(FirebaseDogDto::class.java)
                trySend(dto?.toDomain(snapshot.id))
            }

        awaitClose {
            registration.remove()
        }
    }

    override fun observeDogs(): Flow<PagingData<Dog>> = Pager(
        config = PagingConfig(pageSize = 5),
        pagingSourceFactory = {
            sourceFactory.createPagingSource()
        }
    ).flow

    override fun getDogsDataSource(dogIds: List<String>): PagingSource<*, Dog> {
        return FirestoreDogsDataSource(firestore, dogIds)
    }

    override fun createSubscription(): DogSubscription {
        return FirebaseDogSubscriptionImpl(firestore)
    }
}