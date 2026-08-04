package de.kniederelz.pawplan.user.data.extensions

import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.core.extensions.toTimestamp
import de.kniederelz.pawplan.user.data.FirebaseUserFavoriteDto
import de.kniederelz.pawplan.user.data.FirebaseUserProfileDto
import de.kniederelz.pawplan.user.data.FirebaseUserRoleDto
import de.kniederelz.pawplan.user.domain.UserFavorite
import de.kniederelz.pawplan.user.domain.UserProfile
import de.kniederelz.pawplan.user.domain.UserRole
import de.kniederelz.pawplan.user.domain.UserRole.Companion.fromValue

fun UserProfile.toDto() = FirebaseUserProfileDto(
    userId = userId,
    name = name,
    phoneNumber = phoneNumber,
    birthday = birthday.toTimestamp(),
    volunteerSince = volunteerSince.toTimestamp()
)

fun FirebaseUserFavoriteDto.toDomain(id: String): UserFavorite =
    UserFavorite(
        id = id,
        userId = volunteerId,
        dogId = dogId
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

fun FirebaseUserRoleDto.toDomain(): UserRole = fromValue(role)