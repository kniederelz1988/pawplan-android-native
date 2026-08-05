package de.kniederelz.pawplan.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.appointments.repositories.base.data.FirestoreAppointmentRepositoryImpl
import de.kniederelz.pawplan.appointments.repositories.status.data.FirestoreAppointmentStatusRepositoryImpl
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppointmentStatusModule {
    @Provides
    @Singleton
    fun provideRepository(): AppointmentStatusRepository {
        val firestore = FirebaseFirestore.getInstance()
        return FirestoreAppointmentStatusRepositoryImpl(firestore)
    }
}