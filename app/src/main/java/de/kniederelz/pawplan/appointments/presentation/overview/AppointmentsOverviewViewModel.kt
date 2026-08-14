package de.kniederelz.pawplan.appointments.presentation.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.AppointmentData
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.extensions.isBeforeStartOfDay
import de.kniederelz.pawplan.core.time.ClockProvider
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class AppointmentsOverviewViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val appointmentRatingRepository: AppointmentRatingRepository,
    private val dogRepository: DogRepository,
    private val clockProvider: ClockProvider
) : ViewModel() {

    val appointments = userRepository.userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null)
                return@flatMapLatest flowOf(emptyList())

            appointmentRepository.observeAllVolunteerAppointments(userProfile.id)
        }
        .flatMapLatest { appointments ->
            combine(
                appointmentStatusRepository.observeStatus(appointments.map { it.id }),
                dogRepository.observeDogs(appointments.map { it.dogId }),
                clockProvider.now
            ) { status, dog, _ ->
                appointments.filter { status.containsKey(it.id) && dog.containsKey(it.dogId) }
                    .mapNotNull { appointment ->
                        val appointmentStatus = status[appointment.id]!!

                        if (
                            appointment.date.isBeforeStartOfDay() &&
                            appointmentStatus.status != AppointmentStatusType.PENDING &&
                            appointmentStatus.status != AppointmentStatusType.CONFIRMED
                        )
                            return@mapNotNull null

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
        .asLiveData()

    val favoriteDogs = userRepository.userFavorites
        .flatMapLatest { userFavorites ->
            dogRepository.observeDogs(userFavorites.favorites.map { it.dogId })
        }
        .map { it.values }
        .asLiveData()

    suspend fun completeAppointment(id: String) {
        updateAppointmentState(id, AppointmentStatusType.COMPLETED)
    }
    suspend fun cancelAppointment(id: String) {
        updateAppointmentState(id, AppointmentStatusType.CANCELLED)
    }
    private suspend fun updateAppointmentState(id: String, state: AppointmentStatusType) {
        val userProfile = userRepository.userProfile.value
            ?: return

        appointmentRepository.observeAppointment(id)
            .firstOrNull()?.let { appointment ->
                val status = AppointmentStatus(
                    id = id,
                    appointmentId = id,
                    volunteerId = appointment.volunteerId,
                    dogId = appointment.dogId,
                    status = state,
                    updateAt = LocalDateTime.now(),
                    updatedBy = userProfile.id
                )
                appointmentStatusRepository.createStatus(status)
            }
    }

    suspend fun setRating(id: String, rating: Int, comment: String) {
        appointmentRepository.observeAppointment(id)
            .firstOrNull()?.let { appointment ->
                val rating = AppointmentRating(
                    id = id,
                    appointmentId = appointment.id,
                    dogId = appointment.dogId,
                    volunteerId = appointment.volunteerId,
                    rating = rating,
                    comment = comment,
                    updateAt = LocalDateTime.now()
                )
                appointmentRatingRepository.updateRating(rating)
            }
    }
}