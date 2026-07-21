package de.kniederelz.pawplan.dogs.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.sources.factory.DogDataSourceFactory
import de.kniederelz.pawplan.dogs.domain.DogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseDogRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val dogDataSourceFactory: DogDataSourceFactory,
) : DogRepository {
    companion object {
        const val COLLECTION = "dogs"
    }

    override fun getOverview(): Flow<PagingData<Dog>> = Pager(
        config = PagingConfig(pageSize = 10),
        pagingSourceFactory = {
            dogDataSourceFactory.createPagingSource()
        }
    ).flow

    override suspend fun getDog(dogId: String): Dog? {
        val snapshot = firestore
            .collection(COLLECTION)
            .document(dogId)
            .get()
            .await()

        val dto = snapshot.toObject(FirebaseDogDto::class.java)
        return dto?.toDomain(snapshot.id)
    }

}