package de.kniederelz.pawplan.appointments.repositories.base.data

import com.google.firebase.Timestamp

data class FirebaseAppointmentDto(
    val createdAt: Timestamp = Timestamp.now(),
    val dogId: String = "",
    val volunteerId: String = "",
    val date: Timestamp = Timestamp.now(),
    val type: Int = 0
)
