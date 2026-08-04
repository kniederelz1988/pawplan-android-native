package de.kniederelz.pawplan.dogs.data.sources

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.dogs.data.FirebaseDogDto
import de.kniederelz.pawplan.dogs.data.FirebaseDogRepositoryImpl
import de.kniederelz.pawplan.dogs.data.sources.extensions.toDomain
import de.kniederelz.pawplan.dogs.domain.Dog
import kotlinx.coroutines.tasks.await

class FirestoreDogsDataSource(
    val firestore: FirebaseFirestore,
    val dogIds: List<String>
) : PagingSource<DocumentSnapshot, Dog>() {

    private suspend fun getDogDocuments(dogIds: List<String>, limit: Long, lastDocument: DocumentSnapshot?)
            : Collection<DocumentSnapshot> {
        try {
            var query = firestore
                .collection(FirebaseDogRepositoryImpl.COLLECTION)
                .whereIn(FieldPath.documentId(), dogIds)
                .limit(limit)

            lastDocument?.let {
                query = query.startAfter(it)
            }

            val snapshot = query.get()
                .await()

            return snapshot.documents.mapNotNull { it }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, Dog> {
        return try {
            if (dogIds.isEmpty())
                return LoadResult.Page(data = emptyList(), prevKey = null, nextKey = null)

            val dogDocuments = getDogDocuments(dogIds, params.loadSize.toLong(), params.key)
            val dogs = dogDocuments.mapNotNull { document ->
                document.toObject(FirebaseDogDto::class.java)
                    ?.toDomain(document.id)
            }

            Log.d("FirestoreDogsDataSource", "Loaded dogs: $dogs")

            return LoadResult.Page(
                data = dogs,
                prevKey = null,
                nextKey = dogDocuments.lastOrNull()
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<DocumentSnapshot, Dog>): DocumentSnapshot? = null
}