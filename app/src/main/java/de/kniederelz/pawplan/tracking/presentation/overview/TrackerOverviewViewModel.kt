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
import de.kniederelz.pawplan.tracking.repositories.TrackerAppointmentData
import de.kniederelz.pawplan.tracking.repositories.location.domain.LocationRepository
import de.kniederelz.pawplan.tracking.repositories.reports.domain.IncidentReportRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import de.kniederelz.pawplan.tracking.services.WalkingTrackerState
import de.kniederelz.pawplan.tracking.services.WalkingTrackerStateHolder
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
    private val trackerSessionStateHolder: WalkingTrackerStateHolder,
    private val clockProvider: ClockProvider
) : ViewModel() {

    val routePath = combine(
        trackerSessionStateHolder.state,
        trackerSessionStateHolder.session
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
        trackerSessionStateHolder.state,
        trackerSessionStateHolder.session
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
        trackerSessionStateHolder.state
    ) { location, state ->
        if (state == WalkingTrackerState.Inspect)
            return@combine flowOf(null)

        flowOf(location)
    }
        .flatMapLatest { it }
        .asLiveData()

    val canInspectSession = trackerSessionStateHolder.state
        .map { state -> state != WalkingTrackerState.Started }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    fun toggleInspectedSession(session: WalkingTrackerSession) {
        if (trackerSessionStateHolder.isActive())
            return

        if (trackerSessionStateHolder.state.value == WalkingTrackerState.Inspect
            && trackerSessionStateHolder.session.value?.id == session.id) {
            clearInspectedSession()
            return
        }

        trackerSessionStateHolder.updateState(WalkingTrackerState.Inspect)
        trackerSessionStateHolder.updateSession(session)
    }
    fun clearInspectedSession() {
        if (trackerSessionStateHolder.state.value != WalkingTrackerState.Inspect)
            return

        trackerSessionStateHolder.updateState(WalkingTrackerState.Stopped)
        trackerSessionStateHolder.updateSession(null)
    }

    private val _nextAppointmentFlow = userRepository.userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null)
                return@flatMapLatest flowOf(emptyList())

            appointmentStatusRepository.observeVolunteerStatus(
                userProfile.id,
                listOf(AppointmentStatusType.CONFIRMED)
            )
        }
        .flatMapLatest { statuses ->
            if (statuses.isEmpty())
                return@flatMapLatest flowOf(null)

            combine(
                appointmentRepository.observeAppointments(statuses.map { it.appointmentId }),
                appointmentSessionRepository.observeSessions(statuses.map { it.appointmentId }),
                dogRepository.observeDogs(statuses.map { it.dogId })
            ) { appointments, sessions, dogs ->
                statuses.filter { appointments.containsKey(it.appointmentId) && dogs.containsKey(it.dogId) }
                    .map { appointmentStatus ->
                        val appointment = appointments[appointmentStatus.appointmentId]!!
                        val appointmentSession = sessions[appointmentStatus.appointmentId]

                        val dog = dogs[appointmentStatus.dogId]!!
                        TrackerAppointmentData(
                            appointment.id,
                            appointment,
                            appointmentStatus,
                            dog,
                            null,
                            appointmentSession
                        )
                    }.minByOrNull { it.appointment.date }
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

    val trackingSession = trackerSessionStateHolder.session.asLiveData()

    val reports = combine(
            trackerSessionStateHolder.state,
            trackerSessionStateHolder.session
        ) { state, session ->
            if (state == WalkingTrackerState.Stopped)
                return@combine flowOf(emptyList())

            if (session == null)
                return@combine flowOf(emptyList())

            return@combine incidentReportRepository.getReportsForSession(session.id)
        }
        .flatMapLatest { it }
        .asLiveData()

    val startTrackingEnabled = trackerSessionStateHolder.state
        .combine(_nextAppointmentFlow) { state, appointment ->
            state == WalkingTrackerState.Stopped && appointment != null
                    && appointment.appointment.canStart()
        }
        .asLiveData()
    val stopTrackingEnabled = trackerSessionStateHolder.state
        .map { state -> state == WalkingTrackerState.Started }
        .asLiveData()

    val reportEnabled = trackerSessionStateHolder.state
        .map { state -> state == WalkingTrackerState.Started }
        .asLiveData()

    val userProfile = userRepository.userProfile.asLiveData()
    suspend fun completeAppointment(status: AppointmentStatus) {
        val userProfile = userProfile.value
            ?: return

        val status = status.copy(
            status = AppointmentStatusType.COMPLETED,
            updateAt = LocalDateTime.now(),
            updatedBy = userProfile.id
        )
        appointmentStatusRepository.updateStatus(status)
            .onSuccess { Log.d("TrackerRemarkViewModel", "Status created") }
            .onFailure { Log.d("TrackerRemarkViewModel", "Status creation failed", it) }
    }
}