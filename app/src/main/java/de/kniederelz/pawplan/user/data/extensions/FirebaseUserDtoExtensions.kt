package de.kniederelz.pawplan.user.data.extensions

import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.core.extensions.toTimestamp
import de.kniederelz.pawplan.user.data.FirebaseUserFavoriteDto
import de.kniederelz.pawplan.user.data.FirebaseUserProfileDto
import de.kniederelz.pawplan.user.data.FirebaseUserRoleDto
import de.kniederelz.pawplan.user.domain.UserFavorite
import de.kniederelz.pawplan.user.domain.UserProfile
import de.kniederelz.pawplan.user.domain.UserRole
import de.kniederelz.pawplan.user.domain.UserRoleType

fun FirebaseUserFavoriteDto.toDomain(id: String): UserFavorite =
    UserFavorite(
        id = id,
        userId = volunteerId,
        dogId = dogId
    )

fun UserProfile.toDto() = FirebaseUserProfileDto(
    userId = userId,
    name = name,
    phoneNumber = phoneNumber,
    imageUrl = imageUrl,
    birthday = birthday.toTimestamp(),
    volunteerSince = volunteerSince.toTimestamp()
)
fun FirebaseUserProfileDto.toDomain(id: String): UserProfile =
    UserProfile(
        id = id,
        userId = userId,
        name = name,
        phoneNumber = phoneNumber,
        imageUrl = imageUrl,
        birthday = birthday.toLocalDate(),
        volunteerSince = volunteerSince.toLocalDate(),
    )

fun UserRole.toDto() = FirebaseUserRoleDto(
    role = role.value
)
fun FirebaseUserRoleDto.toDomain(id: String): UserRole =
    UserRole(
        id = id,
        role = UserRoleType.fromValue(role)
    )