package de.kniederelz.pawplan.appointments.repositories.status.data

import com.google.firebase.Timestamp

class FirebaseAppointmentStatusDto(
    val appointmentId: String = "",
    val volunteerId: String = "",
    val dogId: String = "",
    val status: Int = 0,
    val updateAt: Timestamp = Timestamp.now(),
    val updatedBy: String = ""
)