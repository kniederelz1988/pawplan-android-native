package de.kniederelz.pawplan.appointments.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentType
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AppointmentBookingViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    fun createAppointment(dogId: String, date: LocalDateTime) {
        if (dogId.isEmpty())
            return

        val profile = userRepository.userProfile.value ?:
            return

        viewModelScope.launch {
            val appointment = Appointment(
                id = "",

                createdAt = LocalDateTime.now(),
                dogId = dogId,
                volunteerId = profile.id,

                date = date,
                type = AppointmentType.WALK
            )
            appointmentRepository.createAppointment(appointment)
                .onSuccess {
                    Log.d(
                        "AppointmentBookingViewModel",
                        "Appointment created $it"
                    )

                    val appointmentStatus = AppointmentStatus(
                        id = it,

                        appointmentId = it,
                        status = AppointmentStatusType.PENDING,

                        dogId = dogId,
                        volunteerId = profile.id,

                        updatedAt = LocalDateTime.now(),
                        updatedBy = profile.id
                    )
                    appointmentStatusRepository.createStatus(appointmentStatus)
                        .onSuccess {
                            Log.d(
                                "AppointmentBookingViewModel",
                                "Appointment status created"
                            )
                        }
                        .onFailure {
                            Log.e(
                                "AppointmentBookingViewModel",
                                "Failed to create appointment status",
                                it
                            )
                        }
                }
                .onFailure {
                    Log.e(
                        "AppointmentBookingViewModel",
                        "Failed to create appointment",
                        it
                    )
                }
        }
    }
}