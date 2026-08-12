package de.kniederelz.pawplan.user.domain

import de.kniederelz.pawplan.dogs.domain.Dog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val userProfile: StateFlow<UserProfile?>
    val userRole: StateFlow<UserRoleType>
    val userFavorites: StateFlow<UserFavorites>

    suspend fun observeProfiles(profileIds: List<String>): Flow<Map<String, UserProfile>>
    suspend fun observeProfile(profileId: String): Flow<UserProfile>

    suspend fun createProfile(userProfile: UserProfile): Result<String>
    suspend fun updateProfile(userProfile: UserProfile): Result<Unit>

    suspend fun createRole(userRole: UserRole): Result<String>
    suspend fun updateRole(userRole: UserRole): Result<Unit>

    suspend fun createFavorite(userProfile: UserProfile, dog: Dog) : Result<Unit>
    suspend fun deleteFavorite(fav: UserFavorite) : Result<Unit>
}