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
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TrackerRemarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentRemarkRepository: AppointmentRatingRepository,
    private val dogRepository: DogRepository
) : ViewModel() {
    data class Data(
        val appointment: Appointment,
        val dog: Dog
    )

    private val _appointmentSubscription = appointmentRepository.createSubscription()
        .registerListener(checkNotNull(savedStateHandle["appointmentId"]))
    private val _dogSubscription = dogRepository.createSubscription()

    private val _appointmentFlow = _appointmentSubscription.values
        .map { it.values.firstOrNull() }
        .onEach { appointment ->
            appointment?.let { _dogSubscription.registerListener(appointment.dogId) }
        }

    private val _dogFlow = _dogSubscription.values
        .combine(_appointmentFlow) { dogs, appointments ->
            if (appointments == null)
                return@combine null

            return@combine dogs[appointments.dogId]
        }

    val nextAppointment = combine(
            _appointmentFlow,
            _dogFlow
        ) { appointment, dog ->
            if (appointment == null || dog == null)
                return@combine flowOf(null)

            flowOf(Data(appointment, dog))
        }
            .flatMapLatest { it }
            .asLiveData()

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

    override fun onCleared() {
        _appointmentSubscription.close()
        _dogSubscription.close()
    }

}