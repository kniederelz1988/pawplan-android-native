package de.kniederelz.pawplan.dogs.data

import androidx.core.net.toUri
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogGender
import de.kniederelz.pawplan.dogs.domain.DogSize

fun FirebaseDogDto.toDomain(id: String): Dog =
    Dog(
        id = id,
        name = name,
        breed = breed,
        birthday = birthday.toLocalDate(),
        description = description,
        size = DogSize.entries[size],
        gender = DogGender.entries[gender],
        imageURL = imageURL.toUri(),
        shelterDate = shelterDate.toLocalDate(),
        adoptionDateValid = adoptionDateValid,
        adoptionDate = adoptionDate?.toLocalDate()
    )