package de.kniederelz.pawplan.appointments.repositories.status.domain

interface AppointmentStatusRepository {
    suspend fun createStatus(status: AppointmentStatus): Result<String>
    suspend fun updateStatus(status: AppointmentStatus): Result<Unit>

    fun createSubscription(): AppointmentStatusSubscription
}

