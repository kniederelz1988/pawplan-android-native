package de.kniederelz.pawplan.user.domain

import de.kniederelz.pawplan.dogs.domain.Dog

data class UserFavorite(
    val id: String?,
    val dogId: String = "",
    val userId: String = ""
)
data class UserFavorites(
    val favorites: List<UserFavorite> = emptyList()
)

fun UserFavorites.contains(dog: Dog): Boolean {
    val dogId = dog.id ?: return false
    return favorites.any { it.dogId == dogId }
}
fun UserFavorites.get(dog: Dog): UserFavorite? {
    val dogId = dog.id ?: return null
    return favorites.firstOrNull { it.dogId == dogId }
}