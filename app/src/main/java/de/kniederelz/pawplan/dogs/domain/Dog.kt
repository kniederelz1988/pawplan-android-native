package de.kniederelz.pawplan.dogs.domain

import android.net.Uri
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRatingStatistics
import java.time.LocalDate
import java.time.Period

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

    var isFavorite: Boolean = false,
    var statistics: AppointmentRatingStatistics? = null
)
