package de.kniederelz.pawplan.dogs.domain

import android.net.Uri
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingStatistics
import java.time.LocalDate

data class Dog(
    val id: String,

    val name: String,
    val description: String,

    val breed: String,
    val size: DogSize,
    val gender: DogGender,
    val imageURL: Uri,

    val birthday: LocalDate,

    val shelterDate: LocalDate,
    val adoptionDateValid: Boolean,
    val adoptionDate: LocalDate?,

) {
    companion object {
        val EMPTY = Dog(
            "",
            "",
            "",
            "",
            DogSize.SMALL,
            DogGender.MALE,
            Uri.EMPTY,
            LocalDate.now(),
            LocalDate.now(),
            true,
            null
        )
    }
}
