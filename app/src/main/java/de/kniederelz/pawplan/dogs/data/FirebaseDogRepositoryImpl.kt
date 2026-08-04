package de.kniederelz.pawplan.dogs.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.dogs.data.sources.FirestoreDogsDataSource
import de.kniederelz.pawplan.dogs.data.sources.extensions.toDomain
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.sources.factory.DogDataSourceFactory
import de.kniederelz.pawplan.dogs.domain.DogRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseDogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val sourceFactory: DogDataSourceFactory,
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
}