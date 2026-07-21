package de.kniederelz.pawplan.auth.domain

import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<User?>

    suspend fun signIn(
        email: String,
        password: String
    ): Result<User>
    suspend fun signOut() : Result<Unit>

    suspend fun register(
        email: String,
        password: String,
        displayName: String
    ): Result<User>

    suspend fun updateName(
        displayName: String
    ): Result<User>
}

