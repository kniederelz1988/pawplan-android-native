package de.kniederelz.pawplan.appointments.repositories.base.data

import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentType
import de.kniederelz.pawplan.core.extensions.toLocalDateTime
import de.kniederelz.pawplan.core.extensions.toTimestamp

fun FirebaseAppointmentDto.toDomain(id: String) : Appointment =
    Appointment(
        id,
        createdAt.toLocalDateTime(),
        dogId,
        volunteerId,
        date.toLocalDateTime(),
        type = AppointmentType.entries[type]
    )

fun Appointment.toDto() : FirebaseAppointmentDto =
    FirebaseAppointmentDto(
        createdAt.toTimestamp(),
        dogId,
        volunteerId,
        date.toTimestamp(),
        type = AppointmentType.entries.indexOf(type)
    )