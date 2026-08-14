package de.kniederelz.pawplan.activities.permissions

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.core.net.toUri

class MainPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getPermissionState(): MainPermissionState {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationGranted =
                context.checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

            if (!notificationGranted) {
                return MainPermissionState.NeedsNotifications
            }
        }

        return MainPermissionState.Ready
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun notificationPermission() =
        Manifest.permission.POST_NOTIFICATIONS

    fun canScheduleExactAlarms(): Boolean {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                && alarmManager.canScheduleExactAlarms()
    }
    fun requestExactAlarmPermission() {
        if (canScheduleExactAlarms()) {
            return
        }

        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            "package:${context.packageName}".toUri()
        )

        context.startActivity(
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}