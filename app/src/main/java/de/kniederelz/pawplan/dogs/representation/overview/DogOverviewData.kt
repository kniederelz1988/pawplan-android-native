package de.kniederelz.pawplan.dogs.representation.overview

import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingStatistics
import de.kniederelz.pawplan.dogs.domain.Dog

data class DogOverviewData(
    val dog: Dog,
    val statistics: AppointmentRatingStatistics?,
    val isFavorite: Boolean
)

