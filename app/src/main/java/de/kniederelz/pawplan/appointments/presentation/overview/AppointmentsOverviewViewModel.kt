package de.kniederelz.pawplan.appointments.presentation.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.AppointmentData
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.time.ClockProvider
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentsOverviewViewModel @Inject constructor(
    userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val dogRepository: DogRepository,
    private val clockProvider: ClockProvider
) : ViewModel() {

    private val appointmentSubscription
        = appointmentRepository.createVolunteerSubscription()
    private val appointmentStatusSubscription
        = appointmentStatusRepository.createSubscription()
    private val appointmentDogSubscription
        = dogRepository.createSubscription()

    private val _appointmentsFlow = userRepository.userProfileFlow
        .flatMapLatest { userProfile ->
            userProfile?.let {
                Log.d("AppointmentsOverviewViewModel", "User: $it")
                appointmentSubscription.registerListener(it.id)
            }
            appointmentSubscription.values
        }
    private val _appointmentStatusFlow =
        _appointmentsFlow
            .onEach { appointments ->
                appointments.values.forEach { appointment ->
                    appointmentStatusSubscription.registerListener(appointment.id)
                }
            }
            .flatMapLatest {
                appointmentStatusSubscription.values
            }
    private val _appointmentDogFlow =
        _appointmentsFlow
            .onEach { appointments ->
                appointments.values.forEach { appointment ->
                    appointmentDogSubscription.registerListener(appointment.dogId)
                }
            }
            .flatMapLatest {
                appointmentDogSubscription.values
            }

    val appointments = combine(
        _appointmentsFlow,
        _appointmentStatusFlow,
        _appointmentDogFlow,
        clockProvider.now
    ) { appointments, statuses, dogs, _ ->
        appointments.values
            .filter { statuses.containsKey(it.id) && dogs.containsKey(it.dogId) }
            .map {
                val appointmentStatus = statuses[it.id]!!
                val dog = dogs[it.dogId]!!

                AppointmentData(
                    it.id,
                    it,
                    appointmentStatus,
                    dog
                )
            }
    }

    private val favoriteDogsSubscription
        = dogRepository.createSubscription()
    val favoriteDogs = userRepository.userFavoritesFlow
        .flatMapLatest { userFavorites ->
            favoriteDogsSubscription.registerBatchListener( userFavorites.favorites.map { it.dogId }.distinct() )
            favoriteDogsSubscription.values
        }
        .map { it.values }

    fun cancelAppointment(status: AppointmentStatus) {
        viewModelScope.launch {
            val t = status.copy(
                status = AppointmentStatusType.CANCELLED
            )
            appointmentStatusRepository.updateStatus(t)
        }
    }

    override fun onCleared() {
        appointmentSubscription.close()
        appointmentStatusSubscription.close()
        appointmentDogSubscription.close()
    }
}