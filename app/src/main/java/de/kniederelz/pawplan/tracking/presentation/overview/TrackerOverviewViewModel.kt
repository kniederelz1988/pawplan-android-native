package de.kniederelz.pawplan.tracking.presentation.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.base.domain.canStart
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.time.ClockProvider
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.tracking.repositories.base.domain.TrackerAppointmentData
import de.kniederelz.pawplan.tracking.repositories.location.domain.LocationRepository
import de.kniederelz.pawplan.tracking.repositories.reports.domain.IncidentReportRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import de.kniederelz.pawplan.tracking.repositories.state.domain.WalkingTrackerState
import de.kniederelz.pawplan.tracking.repositories.state.domain.WalkingTrackerStateRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class TrackerOverviewViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val appointmentRemarkRepository: AppointmentRatingRepository,
    private val incidentReportRepository: IncidentReportRepository,
    private val dogRepository: DogRepository,
    private val appointmentSessionRepository: WalkingTrackerSessionRepository,
    private val sessionStateRepository: WalkingTrackerStateRepository,
    private val clockProvider: ClockProvider
) : ViewModel() {

    val routePath = combine(
        sessionStateRepository.state,
        sessionStateRepository.session
    ) { state, session ->
        if (session == null || state == WalkingTrackerState.Stopped)
            return@combine flowOf(null)

        val path = session.locations.map { location ->
            GeoPoint(
                location.latitude,
                location.longitude
            )
        }
        flowOf(path)
    }
        .flatMapLatest { it }
        .asLiveData()

    val routeBoundingBox = combine(
        sessionStateRepository.state,
        sessionStateRepository.session
    ) { state, session ->
        if (session == null || state != WalkingTrackerState.Inspect)
            return@combine flowOf(null)

        val path = session.locations.map { location ->
            GeoPoint(
                location.latitude,
                location.longitude
            )
        }
        flowOf(BoundingBox.fromGeoPoints(path))
    }
        .flatMapLatest { it }
        .asLiveData()

    val location = combine(
        locationRepository.location,
        sessionStateRepository.state
    ) { location, state ->
        if (state == WalkingTrackerState.Inspect)
            return@combine flowOf(null)

        flowOf(location)
    }
        .flatMapLatest { it }
        .asLiveData()

    val canInspectSession = sessionStateRepository.state
        .map { state -> state != WalkingTrackerState.Started }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggleInspectedSession(session: WalkingTrackerSession) {
        if (sessionStateRepository.isInspected(session))
            sessionStateRepository.clearInspectSession()
        else
            sessionStateRepository.startInspectSession(session)
    }
    fun clearInspectedSession() {
        sessionStateRepository.clearInspectSession()
    }

    private val _nextAppointmentFlow = userRepository.userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null)
                return@flatMapLatest flowOf(emptyList())

            appointmentRepository.observeUpcomingVolunteerAppointments(userProfile.id)
        }
        .flatMapLatest { appointments ->
            if (appointments.isEmpty())
                return@flatMapLatest flowOf(null)

            combine(
                appointmentStatusRepository.observeStatus(appointments.map { it.id }),
                appointmentSessionRepository.observeSessions(appointments.map { it.id }),
                dogRepository.observeDogs(appointments.map { it.dogId })
            ) { statuses, sessions, dogs ->
                appointments.filter { statuses.containsKey(it.id) && dogs.containsKey(it.dogId) }
                    .mapNotNull { appointment ->
                        val appointmentStatus = statuses[appointment.id]!!
                        if (appointmentStatus.status != AppointmentStatusType.CONFIRMED)
                            return@mapNotNull null

                        val appointmentSession = sessions[appointment.id]
                        val dog = dogs[appointment.dogId]!!

                        TrackerAppointmentData(
                            appointment.id,
                            appointment,
                            appointmentStatus,
                            dog,
                            null,
                            appointmentSession
                        )
                    }
                    .minByOrNull { it.appointment.date }
            }
        }
    val nextAppointment = _nextAppointmentFlow.asLiveData()

    private val _completedAppointmentsFlow = userRepository.userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null)
                return@flatMapLatest flowOf(emptyList())

            appointmentStatusRepository.observeVolunteerStatus(
                userProfile.id,
                listOf(AppointmentStatusType.COMPLETED)
            )
        }
        .flatMapLatest { statuses ->
            if (statuses.isEmpty())
                return@flatMapLatest flowOf(emptyList())

            combine(
                appointmentRepository.observeAppointments(statuses.map { it.appointmentId }),
                appointmentRemarkRepository.observeRatings(statuses.map { it.appointmentId }),
                appointmentSessionRepository.observeSessions(statuses.map { it.appointmentId }),
                dogRepository.observeDogs(statuses.map { it.dogId }),
                clockProvider.now
            ) { appointments, ratings, sessions, dogs, _ ->
                statuses.filter { appointments.containsKey(it.appointmentId)
                    && ratings.containsKey(it.appointmentId)
                    && sessions.containsKey(it.appointmentId)
                    && dogs.containsKey(it.dogId)
                }.map { appointmentStatus ->
                    val appointment = appointments[appointmentStatus.appointmentId]!!
                    val appointmentRating = ratings[appointmentStatus.appointmentId]!!
                    val appointmentSession = sessions[appointmentStatus.appointmentId]!!
                    val dog = dogs[appointmentStatus.dogId]!!

                    TrackerAppointmentData(
                        appointment.id,
                        appointment,
                        appointmentStatus,
                        dog,
                        appointmentRating,
                        appointmentSession
                    )
                }
                    .sortedByDescending { it.appointment.date }
            }
        }
    val completedAppointments = _completedAppointmentsFlow.asLiveData()

    val trackingSession = sessionStateRepository.session.asLiveData()

    val reports = combine(
            sessionStateRepository.state,
            sessionStateRepository.session
        ) { state, session ->
            if (state == WalkingTrackerState.Stopped)
                return@combine flowOf(emptyList())

            if (session == null)
                return@combine flowOf(emptyList())

            return@combine incidentReportRepository.getReportsForSession(session.id)
        }
        .flatMapLatest { it }
        .asLiveData()

    val startTrackingEnabled = sessionStateRepository.state
        .combine(_nextAppointmentFlow) { state, appointment ->
            state == WalkingTrackerState.Stopped && appointment != null
                    && appointment.appointment.canStart()
        }
        .asLiveData()
    val stopTrackingEnabled = sessionStateRepository.state
        .map { state -> state == WalkingTrackerState.Started }
        .asLiveData()

    val reportEnabled = sessionStateRepository.state
        .map { state -> state == WalkingTrackerState.Started }
        .asLiveData()

    val userProfile = userRepository.userProfile.asLiveData()
}