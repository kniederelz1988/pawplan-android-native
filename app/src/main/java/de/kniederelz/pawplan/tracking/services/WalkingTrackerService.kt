package de.kniederelz.pawplan.tracking.services

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.tracking.notification.TrackingNotificationManager
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionManager
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionState
import de.kniederelz.pawplan.tracking.repositories.location.domain.LocationRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.getDistance
import de.kniederelz.pawplan.tracking.repositories.session.domain.getDuration
import de.kniederelz.pawplan.tracking.repositories.state.domain.WalkingTrackerStateRepository
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class WalkingTrackerService: LifecycleService() {
    companion object {
        const val NOTIFICATION_ID = 37
        const val EXTRA_APPOINTMENT_ID = "appointmentId"

        const val ACTION_START_TRACKING = "START_TRACKING"
        const val ACTION_STOP_TRACKING = "STOP_TRACKING"

        fun getStartTrackingIntent(context: Context, appointmentId: String): Intent {
            return Intent(context, WalkingTrackerService::class.java).apply {
                action = ACTION_START_TRACKING
                putExtra(EXTRA_APPOINTMENT_ID, appointmentId)
            }
        }
        fun getStopTrackingIntent(context: Context): Intent {
            return Intent(context, WalkingTrackerService::class.java).apply {
                action = ACTION_STOP_TRACKING
            }
        }
    }

    @Inject
    lateinit var permissionManager: WalkingTrackerPermissionManager

    @Inject
    lateinit var notificationManager: TrackingNotificationManager

    @Inject
    lateinit var stateRepository: WalkingTrackerStateRepository
    @Inject
    lateinit var sessionRepository: WalkingTrackerSessionRepository

    @Inject
    lateinit var locationRepository: LocationRepository

    private var session: WalkingTrackerSession = WalkingTrackerSession()

    override fun onCreate() {
        super.onCreate()

        if (permissionManager.getPermissionState() != WalkingTrackerPermissionState.Ready) {
            Log.d("WalkingTrackerService", "Permissions not ready")
            stopSelf()
            return
        }

        if (stateRepository.isTracking()) {
            Log.d("WalkingTrackerService", "Tracking already active")
            stopSelf()
            return
        }

        notificationManager.createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (permissionManager.getPermissionState() != WalkingTrackerPermissionState.Ready) {
            stopSelf()
            return START_NOT_STICKY
        }

        when (intent?.action) {
            ACTION_START_TRACKING -> {
                val appointmentId = intent.getStringExtra(EXTRA_APPOINTMENT_ID)
                appointmentId?.let {
                    onActionStartTracking(appointmentId)
                }
            }
            ACTION_STOP_TRACKING -> {
                onActionStopTracking()
            }
        }

        return START_STICKY
    }

    private fun onActionStartTracking(id: String) {
        val notification = notificationManager.createNotification(
            getString(R.string.walkingtracker_notification_title),
            getString(
                R.string.walkingtracker_notification_text,
                0.0, 0L
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                notification
            )
        }

        startTrackingSession(id)
        updateTrackingSession()
    }
    private fun onActionStopTracking() {
        lifecycleScope.launch {
            stopTrackingSession()
            sessionRepository.updateSession(session)

            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
    }

    private fun startTrackingSession(id: String) {
        session = WalkingTrackerSession(id = id, startTimestamp = LocalDateTime.now())
        stateRepository.startSession(session)
    }
    private fun updateTrackingSession() {
        lifecycleScope.launch {
            locationRepository.location.collect { location ->
                session = session.copy(
                    locations = session.locations.plus(location)
                )
                stateRepository.updateSession(session)

                notificationManager.updateNotification(
                    getString(R.string.walkingtracker_notification_title),
                    getString(
                        R.string.walkingtracker_notification_text,
                        session.getDistance() / 1000, session.getDuration()
                    )
                )
            }
        }
    }
    private fun stopTrackingSession() {
        session = session.copy(endTimestamp = LocalDateTime.now())
        stateRepository.stopSession(session)

        stateRepository.queueCompletedSession(session)
    }
}