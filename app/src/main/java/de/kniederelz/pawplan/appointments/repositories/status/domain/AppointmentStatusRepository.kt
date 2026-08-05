package de.kniederelz.pawplan.appointments.repositories.status.domain

import kotlinx.coroutines.flow.Flow

interface AppointmentStatusRepository {
    suspend fun createStatus(status: AppointmentStatus): Result<String>
    suspend fun updateStatus(status: AppointmentStatus): Result<Unit>

    fun observeStatus(appointmentId: String): Flow<AppointmentStatus?>
}