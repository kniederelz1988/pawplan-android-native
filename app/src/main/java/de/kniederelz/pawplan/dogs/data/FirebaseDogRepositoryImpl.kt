package de.kniederelz.pawplan.dogs.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class FirebaseDogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : DogRepository {
    companion object {
        const val COLLECTION = "dogs"
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

                val dog = snapshot.toObject(FirebaseDogDto::class.java)
                    ?.toDomain(snapshot.id)

                trySend(dog)
            }

        awaitClose {
            registration.remove()
        }
    }
    override fun observeDogs(dogIds: List<String>): Flow<Map<String, Dog>> {
        return combine(
            dogIds.map { observeDog(it) }
        ) { dogs ->
            dogs.filterNotNull().associateBy { it.id }
        }
    }

    override fun observeAdoptableDogs() = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .whereEqualTo("adoptionDateValid", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    trySend(emptyMap())
                    return@addSnapshotListener
                }

                val dogs = snapshot.documents.mapNotNull {
                    it.toObject(FirebaseDogDto::class.java)
                        ?.toDomain(it.id)
                }

                trySend(dogs.associateBy { it.id })
            }

        awaitClose {
            registration.remove()
        }
    }
}