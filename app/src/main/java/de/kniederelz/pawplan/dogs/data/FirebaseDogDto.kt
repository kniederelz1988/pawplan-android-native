package de.kniederelz.pawplan.dogs.data

import com.google.firebase.Timestamp

data class FirebaseDogDto(
    val id: String? = null,

    val name: String = "",
    val description: String = "",

    val breed: String = "",
    val size: Int = 0,
    val gender: Int = 0,
    val imageURL: String = "",

    val birthday: Timestamp = Timestamp.now(),

    val shelterDate: Timestamp = Timestamp.now(),
    val adoptionDateValid: Boolean = false,
    val adoptionDate: Timestamp? = null
)
