package de.kniederelz.pawplan.tracking.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WalkingTrackerPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getPermissionState(): WalkingTrackerPermissionState {
        val fineGranted =
            context.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            context.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted || !coarseGranted) {
            return WalkingTrackerPermissionState.NeedsForegroundLocation
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val backgroundGranted =
                context.checkSelfPermission(
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (!backgroundGranted) {
                return WalkingTrackerPermissionState.NeedsBackgroundLocation
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationGranted =
                context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!notificationGranted) {
                return WalkingTrackerPermissionState.NeedsNotifications
            }
        }

        return WalkingTrackerPermissionState.Ready
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun notificationPermission() =
        Manifest.permission.POST_NOTIFICATIONS

    fun foregroundPermissions() = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    fun backgroundPermission() =
        Manifest.permission.ACCESS_BACKGROUND_LOCATION

}