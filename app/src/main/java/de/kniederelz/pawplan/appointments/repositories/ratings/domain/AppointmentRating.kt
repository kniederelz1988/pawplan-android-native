package de.kniederelz.pawplan.appointments.repositories.ratings.domain

import java.time.LocalDateTime

data class AppointmentRating(
    val id: String,
    val appointmentId: String,
    val dogId: String,
    val volunteerId: String,
    val rating: Int,
    val comment: String,
    val updatedAt: LocalDateTime,

    val volunteerName: String = ""
)