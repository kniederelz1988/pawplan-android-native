package de.kniederelz.pawplan.appointments.repositories.status.domain

import java.time.LocalDateTime

data class AppointmentStatus(
    val id: String,
    val appointmentId: String,
    val volunteerId: String,
    val dogId: String,
    val status: AppointmentStatusType,
    val updateAt: LocalDateTime,
    val updatedBy: String
) {
    companion object {
        val EMPTY = AppointmentStatus(
            "",
            "",
            "",
            "",
            AppointmentStatusType.PENDING,
            LocalDateTime.now(),
            ""
        )
    }
}
