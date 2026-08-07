package de.kniederelz.pawplan.tracking.permissions

interface WalkingTrackerPermissionManager {
    fun getPermissionState(): WalkingTrackerPermissionState

    fun foregroundPermissions(): Array<String>
    fun backgroundPermission(): String

    fun notificationPermission(): String
}