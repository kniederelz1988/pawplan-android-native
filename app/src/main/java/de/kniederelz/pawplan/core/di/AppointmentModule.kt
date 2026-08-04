package de.kniederelz.pawplan.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.appointments.repositories.base.data.FirestoreAppointmentRepositoryImpl
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppointmentModule {
    @Provides
    @Singleton
    fun provideRepository(): AppointmentRepository {
        val firestore = FirebaseFirestore.getInstance()
        return FirestoreAppointmentRepositoryImpl(firestore)
    }
}