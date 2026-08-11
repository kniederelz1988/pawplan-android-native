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
import kotlinx.coroutines.flow.onEach
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
    private val trackerSessionRepository: WalkingTrackerSessionRepository,
    private val trackerSessionStateHolder: WalkingTrackerStateHolder
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

    private val _confirmedStatusSubscription =
        appointmentStatusRepository.createSubscriptionFilteredByStatus(
            listOf(AppointmentStatusType.CONFIRMED)
        )
    private val _completedStatusSubscription =
        appointmentStatusRepository.createSubscriptionFilteredByStatus(
            listOf(AppointmentStatusType.COMPLETED)
        )

    private val _appointmentSubscription = appointmentRepository.createSubscription()

    private val _remarkSubscription = appointmentRemarkRepository.createSubscription()
    private val _dogSubscription = dogRepository.createSubscription()
    private val _sessionSubscription = trackerSessionRepository.createSubscription()

    private val _confirmedStatusFlow = userRepository.userProfileFlow
        .flatMapLatest { userProfile ->
            userProfile?.let {
                _confirmedStatusSubscription.registerListener(it.id)
            }
            _confirmedStatusSubscription.values
        }
        .onEach { statuses ->
            statuses.values.forEach { status ->
                _appointmentSubscription.registerListener(status.appointmentId)
                _remarkSubscription.registerListener(status.appointmentId)
                _dogSubscription.registerListener(status.dogId)
                _sessionSubscription.registerListener(status.appointmentId)
            }
        }

    private val _completedStatusFlow = userRepository.userProfileFlow
        .flatMapLatest { userProfile ->
            userProfile?.let {
                _completedStatusSubscription.registerListener(it.id)
            }
            _completedStatusSubscription.values
        }
        .onEach { statuses ->
            statuses.values.forEach { status ->
                _appointmentSubscription.registerListener(status.appointmentId)
                _remarkSubscription.registerListener(status.appointmentId)
                _dogSubscription.registerListener(status.dogId)
                _sessionSubscription.registerListener(status.appointmentId)
            }
        }

    private val _appointmentFlow = _appointmentSubscription.values

    private val _remarkFlow = _remarkSubscription.values
    private val _dogFlow = _dogSubscription.values
    private val _sessionFlow = _sessionSubscription.values

    private val _nextAppointmentFlow = combine(
        _confirmedStatusFlow,
        _appointmentFlow,
        _dogFlow,
        _sessionFlow
    ) { status, appointments, dogs, sessions ->
        status.values
            .filter { appointments.containsKey(it.appointmentId) && dogs.containsKey(it.dogId) }
            .map { appointmentStatus ->
                val appointment = appointments[appointmentStatus.appointmentId]!!
                val dog = dogs[appointmentStatus.dogId]!!
                val trackingSession = sessions[appointmentStatus.id]

                TrackerAppointmentData(
                    appointment.id,
                    appointment,
                    appointmentStatus,
                    null,
                    dog,
                    trackingSession
                )
            }
            .minByOrNull { it.appointment.date }
    }
    val nextAppointment = _nextAppointmentFlow.asLiveData()

    private val _completedAppointmentFlow = combine(
        _completedStatusFlow,
        _appointmentFlow,
        _remarkFlow,
        _dogFlow,
        _sessionFlow
    ) { status, appointments, remark, dogs, sessions ->
        status.values
            .filter {
                appointments.containsKey(it.appointmentId)
                    && remark.containsKey(it.appointmentId)
                    && dogs.containsKey(it.dogId)
                    && sessions.containsKey(it.appointmentId)
            }
            .map { appointmentStatus ->
                val appointment = appointments[appointmentStatus.appointmentId]!!
                val dog = dogs[appointmentStatus.dogId]!!
                val appointmentRating = remark[appointmentStatus.id]!!
                val trackingSession = sessions[appointmentStatus.id]

                TrackerAppointmentData(
                    appointment.id,
                    appointment,
                    appointmentStatus,
                    appointmentRating,
                    dog,
                    trackingSession
                )
            }
            .sortedByDescending { it.appointment.date }
    }
    val completedAppointments = _completedAppointmentFlow.asLiveData()

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

    suspend fun completeAppointment(status: AppointmentStatus) {
        val userProfile = userRepository.getUserProfile()
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

    override fun onCleared() {
        _confirmedStatusSubscription.close()
        _completedStatusSubscription.close()
        _appointmentSubscription.close()
        _remarkSubscription.close()
        _dogSubscription.close()
        _sessionSubscription.close()
    }
}