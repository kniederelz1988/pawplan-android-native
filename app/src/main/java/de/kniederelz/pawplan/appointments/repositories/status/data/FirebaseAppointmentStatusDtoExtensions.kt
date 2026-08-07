package de.kniederelz.pawplan.appointments.repositories.status.data

import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.extensions.toLocalDateTime
import de.kniederelz.pawplan.core.extensions.toTimestamp

fun FirebaseAppointmentStatusDto.toDomain(id: String) : AppointmentStatus =
    AppointmentStatus(
        id,
        appointmentId,
        volunteerId,
        dogId,
        AppointmentStatusType.entries[status],
        updateAt.toLocalDateTime(),
        updatedBy
    )

fun AppointmentStatus.toDto() : FirebaseAppointmentStatusDto =
    FirebaseAppointmentStatusDto(
        appointmentId,
        volunteerId,
        dogId,
        AppointmentStatusType.entries.indexOf(status),
        updateAt.toTimestamp(),
        updatedBy
    )