package de.kniederelz.pawplan.tracking.presentation.overview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.tracking.repositories.TrackerAppointmentData
import de.kniederelz.pawplan.tracking.repositories.location.domain.LocationRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import de.kniederelz.pawplan.tracking.services.WalkingTrackerStateHolder
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class TrackerOverviewViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val appointmentRemarkRepository: AppointmentRatingRepository,
    private val dogRepository: DogRepository,
    private val trackerSessionRepository: WalkingTrackerSessionRepository,
    private val trackerSessionStateHolder: WalkingTrackerStateHolder
) : ViewModel() {

    val location = locationRepository.location

    val state = trackerSessionStateHolder.state
    val session = trackerSessionStateHolder.session

    private val _appointmentSubscription = appointmentRepository.createSubscription()
    private val _appointmentStatusSubscription = appointmentStatusRepository.createVolunteerSubscription()
    private val _appointmentRemarkSubscription = appointmentRemarkRepository.createSubscription()
    private val _dogSubscription = dogRepository.createSubscription()
    private val _sessionSubscription = trackerSessionRepository.createSubscription()

    val _appointmentStatus = userRepository.userProfile
        .flatMapLatest { userProfile ->
            userProfile?.let {
                _appointmentStatusSubscription.registerListener(it.id)
            }
            _appointmentStatusSubscription.values
        }.onEach { appointmentStatus ->
            appointmentStatus.values.forEach { status ->
                _appointmentSubscription.registerListener(status.appointmentId)
                _dogSubscription.registerListener(status.dogId)
                _sessionSubscription.registerListener(status.appointmentId)
            }
        }

    val nextAppointment = combine(
        _appointmentStatus,
        _appointmentSubscription.values,
        _dogSubscription.values,
        _sessionSubscription.values
    ) { _, appointments, dogs, sessions ->
        appointments.values.filter { appointments.containsKey(it.id) && dogs.containsKey(it.dogId) }.map { appointment ->
            val dog = dogs[appointment.dogId]!!
            val trackingSession = sessions[appointment.id]

            TrackerAppointmentData(
                appointment.id,
                appointment,
                null,
                dog,
                trackingSession
            )
        }.minByOrNull { it.appointment.date }
    }


    suspend fun createRating(rating: AppointmentRating) {
        appointmentRemarkRepository.createRating(rating)
    }

    override fun onCleared() {
        _appointmentStatusSubscription.close()
        _appointmentSubscription.close()
        _dogSubscription.close()
        _sessionSubscription.close()
    }
}