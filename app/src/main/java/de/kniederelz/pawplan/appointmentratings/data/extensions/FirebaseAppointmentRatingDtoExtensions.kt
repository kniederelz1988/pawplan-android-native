package de.kniederelz.pawplan.appointmentratings.data.extensions

import de.kniederelz.pawplan.appointmentratings.data.FirebaseAppointmentRatingDto
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRating
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.core.extensions.toTimestamp

fun FirebaseAppointmentRatingDto.toDomain(id: String) : AppointmentRating =
    AppointmentRating(
        id,
        appointmentId,
        dogId,
        volunteerId,
        rating,
        comment,
        updatedAt.toLocalDate()
    )

fun AppointmentRating.toDto() : FirebaseAppointmentRatingDto =
    FirebaseAppointmentRatingDto(
        appointmentId,
        dogId,
        volunteerId,
        rating,
        comment,
        updatedAt.toTimestamp()
    )