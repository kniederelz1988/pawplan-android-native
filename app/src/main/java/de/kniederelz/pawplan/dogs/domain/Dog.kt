package de.kniederelz.pawplan.dogs.domain

import android.net.Uri
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.Period

data class Dog(
    val id: String,

    val name: String,
    val description: String,

    val breed: String,
    val size: Int,
    val gender: Int,
    val imageURL: Uri,

    val birthday: LocalDate,

    val shelterDate: LocalDate,
    val adoptionDateValid: Boolean,
    val adoptionDate: LocalDate?,

    var isFavorite: Boolean = false
)

data class DogAge(
    val years: Int,
    val months: Int
)

fun Dog.getAge(): DogAge {
    val period = Period.between(birthday, LocalDate.now())
    return DogAge(period.years, period.months)
}
