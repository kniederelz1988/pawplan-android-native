package de.kniederelz.pawplan.auth

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import de.kniederelz.pawplan.auth.domain.AuthRepository
import de.kniederelz.pawplan.auth.domain.AuthState
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.math.log

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    val authState = authRepository.currentUser
        .map { user ->
            AuthState(
                isAuthenticated = user != null,
                user = user
            )
        }
        .asLiveData()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.signIn(email, password)
                .onSuccess { Log.d("AuthLoginViewModel", "Login successful") }
                .onFailure { Log.d("AuthLoginViewModel", "Login failed. " +
                        "\n Error: ${it.message}" + "\n Cause: ${it.cause}" + "\n Stacktrace: ${it.stackTrace}"
                ) }
        }
    }
    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
                .onSuccess { Log.d("AuthLoginViewModel", "Logout successful") }
        }
    }
}