package de.kniederelz.pawplan.user.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.auth.domain.AuthRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.getDistance
import de.kniederelz.pawplan.user.domain.UserProfile
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val sessionRepository: WalkingTrackerSessionRepository
) : ViewModel() {
    data class ProfileStatistics(
        val appointmentCount: Int = 0,
        val trackedDistance: Double = 0.0,
        val favoriteCount: Int = 0
    )

    val userProfile = userRepository.userProfile.asLiveData()
    val userRole = userRepository.userRole.asLiveData()

    val userStatistics = userRepository.userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null)
                return@flatMapLatest flowOf(ProfileStatistics())

            appointmentStatusRepository.observeVolunteerStatus(
                userProfile.id,
                listOf(AppointmentStatusType.COMPLETED)
            )
                .flatMapLatest { status ->
                    if (status.isEmpty())
                        return@flatMapLatest flowOf(ProfileStatistics())

                    sessionRepository.observeSessions(status.map { it.id })
                        .mapLatest { sessions ->
                            ProfileStatistics(
                                appointmentCount = status.size,
                                trackedDistance = sessions.values.sumOf { it.getDistance() }
                            )
                        }
                }
        }
            .combine(userRepository.userFavorites) { stats, userFavorites ->
                stats.copy(favoriteCount = userFavorites.favorites.size)
            }
            .asLiveData()

    fun updateUserName(name: String) {
        viewModelScope.launch {
            authRepository.updateName(name)
                .onSuccess { Log.d("ProfileViewModel", "Name updated successfully") }
                .onFailure { Log.e("ProfileViewModel", "Failed to update name", it) }
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            userRepository.updateProfile(profile)
                .onSuccess { Log.d("ProfileViewModel", "Profile updated successfully") }
                .onFailure { Log.e("ProfileViewModel", "Failed to update profile", it) }
        }
    }
}