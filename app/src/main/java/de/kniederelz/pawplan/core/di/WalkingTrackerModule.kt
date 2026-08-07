package de.kniederelz.pawplan.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.tracking.repositories.session.data.FirestoreWalkingTrackerSessionRepositoryImpl
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import de.kniederelz.pawplan.tracking.services.WalkingTrackerStateHolder
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WalkingTrackerModule {
    @Provides
    @Singleton
    fun provideRepository(): WalkingTrackerSessionRepository {
        return FirestoreWalkingTrackerSessionRepositoryImpl(
            FirebaseFirestore.getInstance()
        )
    }

    @Provides
    @Singleton
    fun provideHolder(): WalkingTrackerStateHolder {
        return WalkingTrackerStateHolder()
    }
}
