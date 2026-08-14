package de.kniederelz.pawplan.appointments.notifications

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.tracking.services.WalkingTrackerService

class AppointmentNotificationReceiver : BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("id")
            ?: return

        val type = intent.getSerializableExtra(
            "type",
            AppointmentNotificationType::class.java
        )
            ?: return

        val notification = when (type) {
            AppointmentNotificationType.REMINDER -> {
                NotificationCompat.Builder(
                    context,
                    AppointmentNotificationManager.NOTIFICATION_CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_calendar)
                    .setContentTitle("Reminder")
                    .setContentText("Reminder for appointment")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .build()
            }
            AppointmentNotificationType.TRACKING -> {
                NotificationCompat.Builder(
                    context,
                    AppointmentNotificationManager.NOTIFICATION_CHANNEL_ID
                )
                    .setSmallIcon(R.drawable.ic_calendar)
                    .setContentTitle("Appointment coming up")
                    .setContentText("Start tracking your walk?")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(
                        R.drawable.navigation_tracker,
                        "Start",
                        PendingIntent.getService(
                            context,
                            id.hashCode(),
                            WalkingTrackerService.getStartTrackingIntent(context, id),
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    )
                    .setAutoCancel(true)
                    .build()
            }
        }

        NotificationManagerCompat.from(context)
            .notify(31 * type.hashCode() + id.hashCode(), notification)
    }
}