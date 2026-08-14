package de.kniederelz.pawplan.tracking.repositories.base.domain

import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession

data class TrackerAppointmentData(
    val id: String,

    val appointment: Appointment,
    val appointmentStatus: AppointmentStatus,

    val dog: Dog,

    val appointmentRating: AppointmentRating? = null,
    val appointmentSession: WalkingTrackerSession? = null
)