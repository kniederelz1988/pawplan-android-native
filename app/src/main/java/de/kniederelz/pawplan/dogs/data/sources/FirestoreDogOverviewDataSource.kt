package de.kniederelz.pawplan.dogs.data.sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.dogs.data.FirebaseDogDto
import de.kniederelz.pawplan.dogs.data.FirebaseDogRepositoryImpl
import de.kniederelz.pawplan.dogs.data.sources.extensions.toDomain
import de.kniederelz.pawplan.dogs.domain.Dog
import kotlinx.coroutines.tasks.await

class FirestoreDogOverviewDataSource(
    val firestore: FirebaseFirestore
) : PagingSource<DocumentSnapshot, Dog>() {

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Dog> {
        return try {

            var query = firestore
                .collection(FirebaseDogRepositoryImpl.COLLECTION)
                .limit(params.loadSize.toLong())

            params.key?.let { lastDocument ->
                query = query.startAfter(lastDocument)
            }

            val snapshot = query.get()
                .await()

            val dogs = snapshot.documents.mapNotNull { document ->
                val dto = document.toObject(FirebaseDogDto::class.java)
                dto?.toDomain(document.id)
            }

            LoadResult.Page(
                data = dogs,
                prevKey = null,
                nextKey = snapshot.documents.lastOrNull()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Dog>): DocumentSnapshot? = null
}