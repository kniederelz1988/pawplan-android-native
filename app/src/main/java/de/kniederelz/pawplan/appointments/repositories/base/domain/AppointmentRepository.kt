package de.kniederelz.pawplan.appointments.repositories.base.domain

import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    suspend fun createAppointment(appointment: Appointment) : Result<String>
    suspend fun updateAppointment(appointment: Appointment) : Result<Unit>

    fun createSubscription(): RepositorySubscription<Appointment>
    fun createVolunteerSubscription(): RepositorySubscription<Appointment>
}