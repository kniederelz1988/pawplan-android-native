package de.kniederelz.pawplan.tracking.services

import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkingTrackerStateHolder @Inject constructor() {
    private val _state =
        MutableStateFlow<WalkingTrackerState>(WalkingTrackerState.Stopped)
    val state: StateFlow<WalkingTrackerState> = _state

    private val _session =
        MutableStateFlow<WalkingTrackerSession?>(null)
    val session: StateFlow<WalkingTrackerSession?> = _session

    fun updateState(state: WalkingTrackerState) {
        _state.update { state }
    }
    fun updateSession(session: WalkingTrackerSession) {
        _session.update { session }
    }

    fun isActive(): Boolean {
        return state.value == WalkingTrackerState.Started
    }
}