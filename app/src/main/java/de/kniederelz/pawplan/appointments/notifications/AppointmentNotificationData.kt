package de.kniederelz.pawplan.appointments.notifications

import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import java.time.Instant

data class AppointmentNotificationData(
    val id: String,

    val time: Instant,
    val status: AppointmentStatusType
)
