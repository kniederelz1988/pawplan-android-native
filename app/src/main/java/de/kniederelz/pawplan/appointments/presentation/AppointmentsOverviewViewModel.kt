package de.kniederelz.pawplan.appointments.presentation

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

    private val _appointments = userRepository.userProfile
        .flatMapLatest { userProfile ->
            userProfile?.let {
                Log.d("AppointmentsOverviewViewModel", "User: $it")
                appointmentSubscription.registerListener(it.id)
            }
            appointmentSubscription.values
        }
        .onEach { appointments ->
            appointments.values.forEach { appointment ->
                appointmentStatusSubscription.registerListener(appointment.id)
                appointmentDogSubscription.registerListener(appointment.dogId)
            }
        }

    val appointments = combine(
        _appointments,
        appointmentStatusSubscription.values,
        appointmentDogSubscription.values,
        clockProvider.now
    ) { appointments, statuses, dogs, _ ->
        Log.d("AppointmentsOverviewViewModel", "Appointments: ${appointments.count()}")
        Log.d("AppointmentsOverviewViewModel", "Statuses: ${statuses.count()}")
        Log.d("AppointmentsOverviewViewModel", "Dogs: ${dogs.count()}")

        appointments.values.filter { statuses.containsKey(it.id) && dogs.containsKey(it.dogId) }.map {
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
    val favoriteDogs = userRepository.userFavorites
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