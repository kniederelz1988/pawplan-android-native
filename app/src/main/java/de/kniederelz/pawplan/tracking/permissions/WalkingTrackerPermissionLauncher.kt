package de.kniederelz.pawplan.tracking.permissions

import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class WalkingTrackerPermissionLauncher(
    fragment: Fragment,
    private val onPermissionGranted: () -> Unit
) {
    fun requestNextPermission(permissionManager: WalkingTrackerPermissionManager): Boolean {
        val state = permissionManager.getPermissionState()
        when (state) {
            WalkingTrackerPermissionState.Ready ->
                return true

            WalkingTrackerPermissionState.NeedsForegroundLocation ->
                foregroundPermissionLauncher.launch(
                    permissionManager.foregroundPermissions()
                )

            WalkingTrackerPermissionState.NeedsBackgroundLocation ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    backgroundPermissionLauncher.launch(
                        permissionManager.backgroundPermission()
                    )
                }

            WalkingTrackerPermissionState.NeedsNotifications ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(
                        permissionManager.notificationPermission()
                    )
                }
        }

        return false
    }

    private val foregroundPermissionLauncher =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.values.all { it }) {
                onPermissionGranted()
            }
        }
    private val backgroundPermissionLauncher =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                onPermissionGranted()
            }
        }

    private val notificationPermissionLauncher =
        fragment.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                onPermissionGranted()
            }
        }
}