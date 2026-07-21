package de.kniederelz.pawplan.dogs.data

import androidx.core.net.toUri
import com.google.firebase.Timestamp
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.dogs.domain.Dog

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

fun FirebaseDogDto.toDomain(id: String): Dog =
    Dog(
        id = id,
        name = name,
        breed = breed,
        birthday = birthday.toLocalDate(),
        description = description,
        size = size,
        gender = gender,
        imageURL = imageURL.toUri(),
        shelterDate = shelterDate.toLocalDate(),
        adoptionDateValid = adoptionDateValid,
        adoptionDate = adoptionDate?.toLocalDate()
    )