package de.kniederelz.pawplan.dogs.data.sources.factory

import androidx.paging.PagingSource
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.dogs.data.sources.FirestoreDogOverviewDataSource
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.sources.factory.DogDataSourceFactory
import javax.inject.Singleton

@Singleton
class FirebaseDogOverviewDataSourceFactory(
    private val firestore: FirebaseFirestore
) : DogDataSourceFactory {

    override fun createPagingSource(): PagingSource<DocumentSnapshot, Dog> =
        FirestoreDogOverviewDataSource(firestore = firestore)
}