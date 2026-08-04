package de.kniederelz.pawplan.appointments.repositories.base.domain

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    suspend fun createAppointment(appointment: Appointment) : Result<Unit>
    suspend fun updateAppointment(appointment: Appointment) : Result<Unit>

    fun observeNextAppointment(volunteerId: String): Flow<Appointment?>

    fun getAppointmentDataSource(volunteerId: String): PagingSource<*, AppointmentData>
}