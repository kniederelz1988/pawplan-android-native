package de.kniederelz.pawplan.dogs.representation.remarks

import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.user.domain.UserProfile

data class AppointmentRatingData(
    val rating: AppointmentRating,
    val userProfile: UserProfile
)