package de.kniederelz.pawplan.user.domain

import de.kniederelz.pawplan.dogs.domain.Dog
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val userProfile: StateFlow<UserProfile?>
    val userRole: StateFlow<UserRole>
    val userFavorites: StateFlow<UserFavorites>

    suspend fun getProfileName(volunteerId: String): String

    suspend fun updateProfile(userId: String, user: UserProfile): Result<Unit>

    suspend fun createFavorite(user: UserProfile, dog: Dog) : Result<Unit>
    suspend fun deleteFavorite(fav: UserFavorite) : Result<Unit>

}