package de.kniederelz.pawplan.appointments.repositories.base.domain

import java.time.LocalDateTime

data class Appointment(
    val id: String,
    val createdAt: LocalDateTime,
    val dogId: String,
    val volunteerId: String,
    val date: LocalDateTime,
    val type: AppointmentType
)
