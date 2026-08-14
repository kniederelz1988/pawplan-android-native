package de.kniederelz.pawplan.tracking.repositories.state.domain

sealed interface WalkingTrackerState {
    data object Started : WalkingTrackerState
    data object Stopped : WalkingTrackerState
    data object Inspect : WalkingTrackerState
}