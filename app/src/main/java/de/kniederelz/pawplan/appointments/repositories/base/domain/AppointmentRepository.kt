package de.kniederelz.pawplan.appointments.repositories.base.domain

import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    suspend fun createAppointment(appointment: Appointment) : Result<String>
    suspend fun updateAppointment(appointment: Appointment) : Result<Unit>

    fun observeAppointment(appointmentId: String): Flow<Appointment?>
    fun observeAppointments(appointmentIds: List<String>): Flow<Map<String, Appointment>>

    fun observeAllVolunteerAppointments(volunteerId: String): Flow<List<Appointment>>
    fun observeUpcomingVolunteerAppointments(volunteerId: String): Flow<List<Appointment>>
}