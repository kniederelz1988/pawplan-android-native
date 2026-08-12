package de.kniederelz.pawplan.tracking.services

import android.content.Context
import android.content.Intent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalkingTrackerServiceController @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    companion object {
        const val ACTION_START_TRACKING = "START_TRACKING"
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"
        const val EXTRA_APPOINTMENT_ID = "appointmentId"
    }

    fun startTracking(appointmentId: String) {
        val intent = Intent(context, WalkingTrackerService::class.java).apply {
            action = ACTION_START_TRACKING
            putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
        }

        context.startForegroundService(intent)
    }
    fun stopTracking() {
        val intent = Intent(context, WalkingTrackerService::class.java).apply {
            action = ACTION_STOP_TRACKING
        }

        context.startService(intent)
    }
}