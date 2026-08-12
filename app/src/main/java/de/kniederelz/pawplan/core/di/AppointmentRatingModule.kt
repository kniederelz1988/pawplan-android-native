package de.kniederelz.pawplan.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.appointments.repositories.ratings.data.FirestoreAppointmentRatingRepositoryImpl
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppointmentRatingModule {
    @Provides
    @Singleton
    fun provideRepository(): AppointmentRatingRepository {
        return FirestoreAppointmentRatingRepositoryImpl(
            FirebaseFirestore.getInstance()
        )
    }
}