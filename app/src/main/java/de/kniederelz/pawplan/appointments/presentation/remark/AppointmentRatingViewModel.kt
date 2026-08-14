package de.kniederelz.pawplan.appointments.presentation.remark

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AppointmentRatingViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val dogRepository: DogRepository
) : ViewModel() {

    val appointmentId: String = checkNotNull(
        savedStateHandle[AppointmentRatingFragment.APPOINTMENT_KEY]
    )

    private val _nextAppointmentFlow =
        appointmentRepository.observeAppointment(appointmentId)
            .flatMapLatest { appointment ->
                if (appointment == null)
                    return@flatMapLatest flowOf(null)

                combine(
                    appointmentStatusRepository.observeStatus(appointment.id),
                    dogRepository.observeDog(appointment.dogId)
                ) { appointmentStatus, dog ->
                    if (appointmentStatus == null)
                        return@combine null

                    if (dog == null)
                        return@combine null

                    AppointmentRatingData(appointment, appointmentStatus, dog)
                }
            }
    val nextAppointment = _nextAppointmentFlow.asLiveData()
}

