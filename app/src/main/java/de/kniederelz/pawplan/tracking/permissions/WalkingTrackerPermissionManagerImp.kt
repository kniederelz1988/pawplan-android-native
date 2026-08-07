package de.kniederelz.pawplan.tracking.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkingTrackerPermissionManagerImp @Inject constructor(
    @ApplicationContext private val context: Context
) :
    WalkingTrackerPermissionManager {

    override fun getPermissionState(): WalkingTrackerPermissionState {
        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (!fineGranted || !coarseGranted) {
            return WalkingTrackerPermissionState.NeedsForegroundLocation
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val backgroundGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

            if (!backgroundGranted) {
                return WalkingTrackerPermissionState.NeedsBackgroundLocation
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationGranted =
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!notificationGranted) {
                return WalkingTrackerPermissionState.NeedsNotifications
            }
        }

        return WalkingTrackerPermissionState.Ready
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun notificationPermission() =
        Manifest.permission.POST_NOTIFICATIONS

    override fun foregroundPermissions() = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun backgroundPermission() =
        Manifest.permission.ACCESS_BACKGROUND_LOCATION

}