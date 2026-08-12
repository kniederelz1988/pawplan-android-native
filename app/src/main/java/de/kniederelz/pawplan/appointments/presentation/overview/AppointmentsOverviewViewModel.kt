package de.kniederelz.pawplan.appointments.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
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
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
class AppointmentsOverviewViewModel @Inject constructor(
    userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val dogRepository: DogRepository,
    private val clockProvider: ClockProvider
) : ViewModel() {

    private val _appointmentsFlow = userRepository.userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null)
                return@flatMapLatest flowOf(emptyList())

            appointmentRepository.observeUpcomingVolunteerAppointments(userProfile.id)
        }
        .flatMapLatest { appointments ->
            combine(
                appointmentStatusRepository.observeStatus(appointments.map { it.id }),
                dogRepository.observeDogs(appointments.map { it.dogId }),
                clockProvider.now
            ) { status, dog, _ ->
                appointments.filter { status.containsKey(it.id) && dog.containsKey(it.dogId) }
                    .map { appointment ->
                        val appointmentStatus = status[appointment.id]!!
                        val dog = dog[appointment.dogId]!!

                        AppointmentData(
                            appointment.id,
                            appointment,
                            appointmentStatus,
                            dog
                        )
                    }
            }
        }
    val appointments = _appointmentsFlow.asLiveData()

    val favoriteDogs = userRepository.userFavorites
        .flatMapLatest { userFavorites ->
            dogRepository.observeDogs(userFavorites.favorites.map { it.dogId })
        }
        .map { it.values }
        .asLiveData()

    suspend fun completeAppointment(status: AppointmentStatus) {
        val t = status.copy(
            status = AppointmentStatusType.COMPLETED
        )
        appointmentStatusRepository.updateStatus(t)
    }
    suspend fun cancelAppointment(status: AppointmentStatus) {
        val t = status.copy(
            status = AppointmentStatusType.CANCELLED
        )
        appointmentStatusRepository.updateStatus(t)
    }
}