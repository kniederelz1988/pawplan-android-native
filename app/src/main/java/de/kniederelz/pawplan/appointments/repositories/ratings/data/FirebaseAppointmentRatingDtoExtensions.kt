package de.kniederelz.pawplan.appointments.repositories.ratings.data

import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.core.extensions.toLocalDateTime
import de.kniederelz.pawplan.core.extensions.toTimestamp

fun FirebaseAppointmentRatingDto.toDomain(id: String) : AppointmentRating =
    AppointmentRating(
        id,
        appointmentId,
        dogId,
        volunteerId,
        rating,
        comment,
        updateAt.toLocalDateTime()
    )

fun AppointmentRating.toDto() : FirebaseAppointmentRatingDto =
    FirebaseAppointmentRatingDto(
        appointmentId,
        dogId,
        volunteerId,
        rating,
        comment,
        updateAt.toTimestamp()
    )