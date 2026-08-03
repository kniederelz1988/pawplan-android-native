package de.kniederelz.pawplan.appointmentratings.domain

import java.time.LocalDate

data class AppointmentRating(
    val id: String,
    val appointmentId: String,
    val dogId: String,
    val volunteerId: String,
    val rating: Int,
    val comment: String,
    val updatedAt: LocalDate,

    val volunteerName: String = ""
)