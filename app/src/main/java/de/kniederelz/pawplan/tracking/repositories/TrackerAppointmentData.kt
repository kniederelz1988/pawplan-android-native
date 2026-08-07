package de.kniederelz.pawplan.tracking.repositories

import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession

data class TrackerAppointmentData(
    val id: String,

    val appointment: Appointment,
    val appointmentRating: AppointmentRating?,

    val dog: Dog,

    val trackingSession: WalkingTrackerSession?
)