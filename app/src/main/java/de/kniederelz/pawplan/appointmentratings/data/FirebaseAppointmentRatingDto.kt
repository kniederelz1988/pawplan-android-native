package de.kniederelz.pawplan.appointmentratings.data

import com.google.firebase.Timestamp

class FirebaseAppointmentRatingDto(
    val appointmentId: String = "",
    val dogId: String = "",
    val volunteerId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val updatedAt: Timestamp = Timestamp.now()
)