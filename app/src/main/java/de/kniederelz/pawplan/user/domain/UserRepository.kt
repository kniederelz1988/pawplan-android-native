package de.kniederelz.pawplan.user.domain

import android.util.Log
import de.kniederelz.pawplan.dogs.domain.Dog
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val userProfileFlow: StateFlow<UserProfile?>
    fun getUserProfile(): UserProfile? {
        Log.d("UserRepository", "getUserProfile -> ${userProfileFlow.value}")
        return userProfileFlow.value
    }

    val userRoleFlow: StateFlow<UserRole>
    val userFavoritesFlow: StateFlow<UserFavorites>

    suspend fun getProfileName(volunteerId: String): String

    suspend fun updateProfile(userId: String, user: UserProfile): Result<Unit>

    suspend fun createFavorite(user: UserProfile, dog: Dog) : Result<Unit>
    suspend fun deleteFavorite(fav: UserFavorite) : Result<Unit>

}