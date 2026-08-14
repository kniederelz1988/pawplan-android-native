package de.kniederelz.pawplan.activities.permissions

import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity

class MainPermissionLauncher(
    fragmentActivity: FragmentActivity,
    private val onPermissionGranted: () -> Unit
) {
    fun requestNextPermission(permissionManager: MainPermissionManager): Boolean {
        val state = permissionManager.getPermissionState()
        when (state) {
            MainPermissionState.Ready ->
                return true

            MainPermissionState.NeedsNotifications ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(
                        permissionManager.notificationPermission()
                    )
                }
        }

        return false
    }

    private val notificationPermissionLauncher =
        fragmentActivity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                onPermissionGranted()
            }
        }
}