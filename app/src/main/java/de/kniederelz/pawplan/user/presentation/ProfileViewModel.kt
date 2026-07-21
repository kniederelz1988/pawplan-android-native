package de.kniederelz.pawplan.user.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.auth.domain.AuthRepository
import de.kniederelz.pawplan.user.domain.UserProfile
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.UserRole
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val currentUser = authRepository.currentUser

    val userProfile = userRepository.userProfile
    val userRole = userRepository.userRole

    fun updateUserName(name: String) {
        viewModelScope.launch {
            authRepository.updateName(name)
                .onSuccess { Log.d("ProfileViewModel", "Name updated successfully") }
                .onFailure { Log.e("ProfileViewModel", "Failed to update name", it) }
        }
    }

    fun updateProfile(profile: UserProfile) {
        val user = currentUser.value ?: return

        viewModelScope.launch {
            userRepository.updateProfile(user.uid, profile)
                .onSuccess { Log.d("ProfileViewModel", "Profile updated successfully") }
                .onFailure { Log.e("ProfileViewModel", "Failed to update profile", it) }
        }
    }
}