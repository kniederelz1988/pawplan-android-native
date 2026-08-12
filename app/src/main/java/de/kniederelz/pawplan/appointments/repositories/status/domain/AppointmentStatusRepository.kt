package de.kniederelz.pawplan.appointments.repositories.status.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface AppointmentStatusRepository {
    suspend fun createStatus(status: AppointmentStatus): Result<String>
    suspend fun updateStatus(status: AppointmentStatus): Result<Unit>

    fun observeStatus(appointmentId: String): Flow<AppointmentStatus?>
    fun observeStatus(appointmentIds: List<String>): Flow<Map<String, AppointmentStatus>>
    {
        return combine(
            appointmentIds.map { observeStatus(it) }
        ) { statuses ->
            statuses.filterNotNull().associateBy { it.id }
        }
    }

    fun observeVolunteerStatus(volunteerId: String, status: Collection<AppointmentStatusType>)
        : Flow<List<AppointmentStatus>>
}

