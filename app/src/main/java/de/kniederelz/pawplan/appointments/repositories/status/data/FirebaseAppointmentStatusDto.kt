package de.kniederelz.pawplan.appointments.repositories.status.data

import com.google.firebase.Timestamp

class FirebaseAppointmentStatusDto(
    val appointmentId: String,
    val volunteerId: String,
    val dogId: String,
    val status: Int,
    val updatedAt: Timestamp,
    val updatedBy: String
)