package de.kniederelz.pawplan.activities.permissions

sealed interface MainPermissionState {
    data object Ready : MainPermissionState
    data object NeedsNotifications : MainPermissionState
}