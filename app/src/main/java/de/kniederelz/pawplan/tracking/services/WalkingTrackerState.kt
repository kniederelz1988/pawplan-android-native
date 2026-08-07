package de.kniederelz.pawplan.tracking.services

sealed interface WalkingTrackerState {
    data object Started : WalkingTrackerState
    data object Stopped : WalkingTrackerState
}