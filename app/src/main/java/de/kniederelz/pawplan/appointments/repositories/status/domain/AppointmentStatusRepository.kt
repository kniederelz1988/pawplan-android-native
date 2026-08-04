package de.kniederelz.pawplan.appointments.repositories.status.domain

import kotlinx.coroutines.flow.Flow

interface AppointmentStatusRepository {
    suspend fun createStatus(status: AppointmentStatus)
    suspend fun updateStatus(status: AppointmentStatus)

    fun observeStatus(appointmentId: String): Flow<AppointmentStatus?>
}