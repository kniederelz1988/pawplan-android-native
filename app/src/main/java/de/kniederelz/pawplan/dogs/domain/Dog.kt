package de.kniederelz.pawplan.dogs.domain

import android.net.Uri
import java.time.LocalDate

data class Dog(
    val id: String,

    val name: String,
    val description: String,

    val breed: String,
    val size: DogSizeType,
    val gender: DogGender,
    val imageURL: Uri,

    val birthday: LocalDate,

    val shelterDate: LocalDate,
    val adoptionDateValid: Boolean,
    val adoptionDate: LocalDate?,

    ) {
    companion object
}
