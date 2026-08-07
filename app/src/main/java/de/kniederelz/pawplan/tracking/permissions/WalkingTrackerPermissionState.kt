package de.kniederelz.pawplan.tracking.permissions

sealed interface WalkingTrackerPermissionState {
    data object Ready : WalkingTrackerPermissionState
    data object NeedsForegroundLocation : WalkingTrackerPermissionState
    data object NeedsBackgroundLocation : WalkingTrackerPermissionState
    data object NeedsNotifications : WalkingTrackerPermissionState
}