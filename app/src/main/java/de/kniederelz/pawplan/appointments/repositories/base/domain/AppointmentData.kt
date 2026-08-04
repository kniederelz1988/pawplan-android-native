package de.kniederelz.pawplan.appointments.repositories.base.domain

import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.dogs.domain.Dog

data class AppointmentData(
    val id: String,

    val appointment: Appointment,
    val appointmentStatus: AppointmentStatus,

    val dog: Dog
)
