package de.kniederelz.pawplan.tracking.notification

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import de.kniederelz.pawplan.R
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
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Walking Tracker Informationen"

        const val SERVICE_NOTIFICATION_ID = 1
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
            .setOngoing(true)
            .build()
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun updateNotification(title: String, text: String) {
        NotificationManagerCompat.from(context)
            .notify(SERVICE_NOTIFICATION_ID,createNotification(title, text)
        )
    }
}