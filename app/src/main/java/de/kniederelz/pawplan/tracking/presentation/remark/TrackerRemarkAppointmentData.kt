package de.kniederelz.pawplan.tracking.presentation.remark

import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.dogs.domain.Dog

data class TrackerRemarkAppointmentData(
    val appointment: Appointment,
    val dog: Dog
)