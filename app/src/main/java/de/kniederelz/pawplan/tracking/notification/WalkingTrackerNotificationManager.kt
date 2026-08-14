package de.kniederelz.pawplan.tracking.notification

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.tracking.services.WalkingTrackerService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "WALKING_TRACKER_CHANNEL"
        const val NOTIFICATION_CHANNEL_NAME = "Walking Tracker"
        const val NOTIFICATION_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Walking Tracker Information"
    }

    fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NOTIFICATION_CHANNEL_IMPORTANCE
        )
        channel.description = NOTIFICATION_CHANNEL_DESCRIPTION

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun createNotification(title: String, text: String): Notification {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.navigation_tracker)
            .addAction(
                R.drawable.navigation_tracker,
                "Stop",
                PendingIntent.getService(
                    context,
                    37 * 41,
                    WalkingTrackerService.getStopTrackingIntent(context),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .build()
    }
    @SuppressLint("MissingPermission")
    fun updateNotification(title: String, text: String) {
        NotificationManagerCompat
            .from(context)
            .notify(
                WalkingTrackerService.NOTIFICATION_ID,
                createNotification(title, text)
            )
    }
}