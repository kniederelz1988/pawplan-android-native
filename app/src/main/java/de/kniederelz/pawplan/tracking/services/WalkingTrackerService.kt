package de.kniederelz.pawplan.tracking.services

import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.lifecycle.LifecycleService
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.tracking.notification.TrackingNotificationManager
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionManager
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionState
import de.kniederelz.pawplan.tracking.repositories.location.domain.LocationRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import javax.inject.Inject

@AndroidEntryPoint
class WalkingTrackerService: LifecycleService() {
    @Inject
    lateinit var permissionManager: WalkingTrackerPermissionManager
    @Inject
    lateinit var notificationManager: TrackingNotificationManager

    @Inject
    lateinit var walkingTrackerSessionRepository: WalkingTrackerSessionRepository
    @Inject
    lateinit var locationRepository: LocationRepository

    @Inject
    lateinit var trackingStateHolder: WalkingTrackerStateHolder

    private lateinit var walkingTracker: WalkingTracker

    override fun onCreate() {
        super.onCreate()

        if (permissionManager.getPermissionState() != WalkingTrackerPermissionState.Ready) {
            stopTracking()
            return
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        when (intent?.action) {
            "START_TRACKING" -> {
                val appointmentId = intent.getStringExtra("appointmentId")
                appointmentId?.let {
                    startTracking(WalkingTrackerSession(appointmentId))
                }
            }
            "STOP_TRACKING" -> {
                stopTracking()
            }
        }

        return START_STICKY
    }

    private fun startTracking(session: WalkingTrackerSession) {
        walkingTracker = WalkingTracker(this, session)

        walkingTracker.startTracking()

        notificationManager.createNotificationChannel()
        val notification = notificationManager.createNotification(
            "Walking Tracker",
            "Walking Tracker läuft gerade"
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                TrackingNotificationManager.SERVICE_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(
                TrackingNotificationManager.SERVICE_NOTIFICATION_ID,
                notification
            )
        }
    }
    private fun stopTracking() {
        walkingTracker.stopTracking()

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }
}