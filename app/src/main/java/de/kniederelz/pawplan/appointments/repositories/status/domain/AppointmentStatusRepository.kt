package de.kniederelz.pawplan.appointments.repositories.status.domain

import de.kniederelz.pawplan.core.RepositorySubscription

interface AppointmentStatusRepository {
    suspend fun createStatus(status: AppointmentStatus): Result<String>
    suspend fun updateStatus(status: AppointmentStatus): Result<Unit>

    fun createSubscription(): RepositorySubscription<AppointmentStatus>
    fun createVolunteerSubscription(): RepositorySubscription<AppointmentStatus>
}

