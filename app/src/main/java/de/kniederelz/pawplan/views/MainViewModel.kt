package de.kniederelz.pawplan.views

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.tracking.repositories.state.domain.WalkingTrackerStateRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val appointmentRatingRepository: AppointmentRatingRepository,
    private val stateRepository: WalkingTrackerStateRepository
): ViewModel() {
    val trackingState = stateRepository.state.asLiveData()

    var queuedCompletedSessions = stateRepository.queuedCompletedSession.asLiveData()
    suspend fun removeQueuedCompletedSession(id: String) {
        val session = queuedCompletedSessions.value?.find { it.id == id }
            ?: return

        stateRepository.dequeueCompletedSession(session)
    }

    suspend fun completeAppointment(id: String) {
        val userProfile = userRepository.userProfile.value
            ?: return

        appointmentRepository.observeAppointment(id)
            .firstOrNull()?.let { appointment ->
                val status = AppointmentStatus(
                    id = id,
                    appointmentId = id,
                    volunteerId = appointment.volunteerId,
                    dogId = appointment.dogId,
                    status = AppointmentStatusType.COMPLETED,
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