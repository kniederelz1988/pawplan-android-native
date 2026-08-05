package de.kniederelz.pawplan.appointments.repositories.base.domain

import androidx.paging.PagingSource
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusSubscription
import de.kniederelz.pawplan.dogs.domain.DogSubscription
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    suspend fun createAppointment(appointment: Appointment) : Result<Unit>
    suspend fun updateAppointment(appointment: Appointment) : Result<Unit>

    fun observeNextAppointment(volunteerId: String): Flow<Appointment?>

    fun getAppointmentDataSource(
        volunteerId: String,
        statusSubscription: AppointmentStatusSubscription,
        dogSubscription: DogSubscription
    ): PagingSource<*, Appointment>
}