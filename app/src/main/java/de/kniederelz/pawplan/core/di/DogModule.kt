package de.kniederelz.pawplan.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.dogs.data.sources.factory.FirebaseDogOverviewDataSourceFactory
import de.kniederelz.pawplan.dogs.data.FirebaseDogRepositoryImpl
import de.kniederelz.pawplan.dogs.domain.DogRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DogModule {
    @Provides
    @Singleton
    fun provideRepository(): DogRepository {
        val firestore = FirebaseFirestore.getInstance()
        return FirebaseDogRepositoryImpl(
            firestore = firestore,
            dogDataSourceFactory = FirebaseDogOverviewDataSourceFactory(firestore)
        )
    }
}