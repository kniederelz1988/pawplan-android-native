package de.kniederelz.pawplan.user.data

import de.kniederelz.pawplan.user.domain.UserFavorite

data class FirebaseUserFavoriteDto(
    val dogId: String = "",
    val volunteerId: String = ""
)

fun FirebaseUserFavoriteDto.toDomain(id: String): UserFavorite =
    UserFavorite(
        id = id,
        userId = volunteerId,
        dogId = dogId
    )

