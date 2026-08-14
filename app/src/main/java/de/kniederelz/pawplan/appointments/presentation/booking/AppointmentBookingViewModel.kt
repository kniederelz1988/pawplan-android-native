package de.kniederelz.pawplan.appointments.presentation.booking

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentType
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AppointmentBookingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,

) : ViewModel() {

    private val _dogFlow = dogRepository.observeDog(
        checkNotNull(savedStateHandle["dogId"])
    )

    val dog = _dogFlow.asLiveData()

    val userProfile = userRepository.userProfile.asLiveData()

    fun createAppointment(dog: Dog, date: LocalDateTime) {
        val profile = userProfile.value ?:
            return

        viewModelScope.launch {
            val appointment = Appointment(
                id = "",

                createdAt = LocalDateTime.now(),
                dogId = dog.id,
                volunteerId = profile.id,

                date = date,
                type = AppointmentType.WALK
            )
            appointmentRepository.createAppointment(appointment)
                .onSuccess { it ->
                    val appointmentStatus = AppointmentStatus(
                        id = it,

                        appointmentId = it,
                        status = AppointmentStatusType.CONFIRMED,

                        dogId = dog.id,
                        volunteerId = profile.id,

                        updateAt = LocalDateTime.now(),
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

    fun getDog(): Dog? {
        return dog.value
    }
}