package de.kniederelz.pawplan.appointments.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentType
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AppointmentBookingViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun createAppointment(dogId: String, date: LocalDateTime) {
        if (dogId.isEmpty())
            return

        val profile = userRepository.userProfile.value ?: return

        val appointment = Appointment(
            id = "",

            createdAt = LocalDateTime.now(),
            dogId = dogId,
            volunteerId = profile.userId,

            date = date,
            type = AppointmentType.WALK
        )

        viewModelScope.launch {
            appointmentRepository.createAppointment(appointment)
                .onSuccess { Log.d("AppointmentBookingViewModel", "Appointment created") }
                .onFailure { Log.e("AppointmentBookingViewModel", "Failed to create appointment", it) }
        }
    }
}