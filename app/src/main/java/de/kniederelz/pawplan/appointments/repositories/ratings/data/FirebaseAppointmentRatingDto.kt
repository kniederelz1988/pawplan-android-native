package de.kniederelz.pawplan.appointments.repositories.ratings.data

import com.google.firebase.Timestamp

class FirebaseAppointmentRatingDto(
    val appointmentId: String = "",
    val dogId: String = "",
    val volunteerId: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val updateAt: Timestamp = Timestamp.now()
)