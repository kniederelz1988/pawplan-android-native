package de.kniederelz.pawplan.user.data

import com.google.firebase.Timestamp

data class FirebaseUserProfileDto(
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val birthday: Timestamp = Timestamp.now(),
    val volunteerSince: Timestamp = Timestamp.now(),
)

