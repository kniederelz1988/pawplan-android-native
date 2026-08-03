package de.kniederelz.pawplan.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.appointmentratings.data.FirestoreAppointmentRatingRepositoryImpl
import de.kniederelz.pawplan.appointmentratings.data.sources.factories.FirestoreAppointmentRatingDataSourceFactory
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRatingRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppointmentRatingModule {
    @Provides
    @Singleton
    fun provideRepository(): AppointmentRatingRepository {
        return FirestoreAppointmentRatingRepositoryImpl(
            FirebaseFirestore.getInstance(),
            FirestoreAppointmentRatingDataSourceFactory(FirebaseFirestore.getInstance())
        )
    }
}