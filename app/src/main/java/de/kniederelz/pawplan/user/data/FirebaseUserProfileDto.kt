package de.kniederelz.pawplan.user.data

import com.google.firebase.Timestamp
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.user.domain.UserProfile

data class FirebaseUserProfileDto(
    val userId: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val birthday: Timestamp = Timestamp.now(),
    val volunteerSince: Timestamp = Timestamp.now(),
)

fun FirebaseUserProfileDto.toDomain(id: String): UserProfile =
    UserProfile(
        id = id,
        userId = userId,
        name = name,
        phoneNumber = phoneNumber,
        birthday = birthday.toLocalDate(),
        volunteerSince = volunteerSince.toLocalDate(),
    )