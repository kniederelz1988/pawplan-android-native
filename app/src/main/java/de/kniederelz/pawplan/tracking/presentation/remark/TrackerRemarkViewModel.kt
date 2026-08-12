package de.kniederelz.pawplan.tracking.presentation.remark

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.Appointment
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.dogs.domain.DogRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TrackerRemarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentRemarkRepository: AppointmentRatingRepository,
    private val dogRepository: DogRepository
) : ViewModel() {

    private val _nextAppointmentFlow =
        appointmentRepository.observeAppointment(
            checkNotNull(savedStateHandle["appointmentId"])
        )
            .flatMapLatest { appointment ->
                if (appointment == null)
                    return@flatMapLatest flowOf(null)

                dogRepository.observeDog(appointment.dogId)
                    .flatMapLatest { dog ->
                        if (dog == null)
                            return@flatMapLatest flowOf(null)

                        flowOf(TrackerRemarkAppointmentData(appointment, dog))
                    }
            }
    val nextAppointment = _nextAppointmentFlow.asLiveData()

    fun getAppointment(): Appointment? {
        return nextAppointment.value?.appointment
    }

    suspend fun submitRating(appointment: Appointment, rating: Int, comment: String) {
        val rating = AppointmentRating(
            id = appointment.id,
            appointmentId = appointment.id,
            volunteerId = appointment.volunteerId,
            dogId = appointment.dogId,
            rating = rating,
            comment = comment,
            updateAt = LocalDateTime.now()
        )
        appointmentRemarkRepository.createRating(rating)
            .onSuccess { Log.d("TrackerRemarkViewModel", "Rating created") }
            .onFailure { Log.e("TrackerRemarkViewModel", "Rating creation failed", it) }
    }
}

