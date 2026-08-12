package de.kniederelz.pawplan.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import de.kniederelz.pawplan.auth.domain.AuthRepository
import de.kniederelz.pawplan.auth.domain.AuthState
import de.kniederelz.pawplan.auth.domain.User
import de.kniederelz.pawplan.dogs.domain.DogApi
import de.kniederelz.pawplan.dogs.domain.getUrl
import de.kniederelz.pawplan.user.domain.UserProfile
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.UserRole
import de.kniederelz.pawplan.user.domain.UserRoleType
import kotlinx.coroutines.flow.map

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val dogApi: DogApi
) : ViewModel() {
    val authState = authRepository.currentUser
        .map { user ->
            AuthState(
                isAuthenticated = user != null,
                user = user
            )
        }
        .asLiveData()

    suspend fun login(email: String, password: String) {
        authRepository.signIn(email, password)
            .onSuccess { Log.d("AuthLoginViewModel", "Login successful") }
            .onFailure {
                Log.d(
                    "AuthLoginViewModel", "Login failed. " +
                            "\n Error: ${it.message}" + "\n Cause: ${it.cause}" + "\n Stacktrace: ${it.stackTrace}"
                )
            }
    }

    suspend fun logout() {
        authRepository.signOut()
            .onSuccess { Log.d("AuthLoginViewModel", "Logout successful") }
    }

    suspend fun register(email: String, password: String, name: String) {
        val result = authRepository.register(email, password, name)
            .onSuccess { Log.d("AuthLoginViewModel", "Register successful") }
            .onFailure { Log.e("AuthLoginViewModel", "Register failed. ", it) }

        if (result.isFailure)
            return

        val authState = authState.value
        if (authState == null || !authState.isAuthenticated || authState.user == null)
            return

        createUser(authState.user, name)
    }

    private suspend fun createUser(user: User, name: String) {
        val imageResponse = dogApi.getRandomDogImage()

        val userProfile = UserProfile(
            userId = user.uid,
            name = name,

            imageUrl = imageResponse.getUrl()
        )
        val profileResult = userRepository.createProfile(userProfile)
        if (profileResult.isFailure)
            return

        val profileId = profileResult.getOrNull()
            ?: return

        val userRole = UserRole(
            id = profileId,
            role = UserRoleType.VOLUNTEER
        )
        userRepository.createRole(userRole)
    }
}