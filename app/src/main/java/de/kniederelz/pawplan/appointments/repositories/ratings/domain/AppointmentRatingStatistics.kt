package de.kniederelz.pawplan.appointments.repositories.ratings.domain

data class AppointmentRatingStatistics(
    val id: String,

    val average: Float,
    val count: Int
)