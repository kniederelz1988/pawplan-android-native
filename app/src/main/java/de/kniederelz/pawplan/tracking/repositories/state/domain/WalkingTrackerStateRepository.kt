package de.kniederelz.pawplan.tracking.repositories.state.domain

import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkingTrackerStateRepository @Inject constructor() {
    private val _state =
        MutableStateFlow<WalkingTrackerState>(WalkingTrackerState.Stopped)
    val state: StateFlow<WalkingTrackerState> = _state

    private val _session =
        MutableStateFlow<WalkingTrackerSession?>(null)
    val session: StateFlow<WalkingTrackerSession?> = _session

    private val _queuedCompletedSessions =
        MutableStateFlow<List<WalkingTrackerSession>>(emptyList())
    val queuedCompletedSession : StateFlow<List<WalkingTrackerSession>>
        = _queuedCompletedSessions

    fun isTracking(): Boolean {
        return _state.value == WalkingTrackerState.Started
    }
    fun isTracked(session: WalkingTrackerSession): Boolean {
        if (_state.value != WalkingTrackerState.Started)
            return false

        if (_session.value?.id != session.id)
            return false

        return true
    }
    fun startSession(session: WalkingTrackerSession) {
        if (_state.value != WalkingTrackerState.Stopped)
            return

        if (_session.value?.id == session.id)
            return

        _session.update { session }
        _state.update { WalkingTrackerState.Started }
    }
    fun updateSession(session: WalkingTrackerSession) {
        if (_state.value != WalkingTrackerState.Started)
            return

        if (_session.value?.id != session.id)
            return

        _session.update { session }
    }
    fun stopSession(session: WalkingTrackerSession) {
        if (_state.value != WalkingTrackerState.Started)
            return

        if (_session.value?.id != session.id)
            return

        _session.update { null }
        _state.update { WalkingTrackerState.Stopped }
    }

    fun isInspecting(): Boolean {
        return _state.value == WalkingTrackerState.Inspect
    }
    fun isInspected(session: WalkingTrackerSession) : Boolean {
        if (_state.value != WalkingTrackerState.Inspect)
            return false

        if (_session.value?.id != session.id)
            return false

        return true
    }
    fun startInspectSession(session: WalkingTrackerSession) {
        if (_state.value != WalkingTrackerState.Stopped)
            return

        _session.update { session }
        _state.update { WalkingTrackerState.Inspect }
    }
    fun clearInspectSession() {
        if (_state.value != WalkingTrackerState.Inspect)
            return

        _session.update { null }
        _state.update { WalkingTrackerState.Stopped }
    }

    fun queueCompletedSession(session: WalkingTrackerSession) {
        if (_queuedCompletedSessions.value.contains(session))
            return

        _queuedCompletedSessions.update { it.plus(session) }
    }
    fun dequeueCompletedSession(session: WalkingTrackerSession) {
        if (!_queuedCompletedSessions.value.contains(session))
            return

        _queuedCompletedSessions.update { it.minus(session) }
    }
}