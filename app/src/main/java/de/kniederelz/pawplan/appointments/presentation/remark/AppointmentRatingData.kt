package de.kniederelz.pawplan.appointments.presentation.remark

import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.dogs.domain.Dog

data class AppointmentRatingData(
    val appointment: Appointment,
    val dog: Dog
)