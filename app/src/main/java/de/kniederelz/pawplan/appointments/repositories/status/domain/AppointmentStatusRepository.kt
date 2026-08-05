package de.kniederelz.pawplan.appointments.repositories.status.domain

interface AppointmentStatusRepository {
    suspend fun createStatus(status: AppointmentStatus)
    suspend fun updateStatus(status: AppointmentStatus)

    fun createSubscription(): AppointmentStatusSubscription
}

