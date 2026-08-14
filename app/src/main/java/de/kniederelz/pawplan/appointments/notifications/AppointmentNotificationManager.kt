package de.kniederelz.pawplan.appointments.notifications

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "APPOINTMENT_CHANNEL"
        const val NOTIFICATION_CHANNEL_NAME = "Appointment"
        const val NOTIFICATION_CHANNEL_IMPORTANCE = NotificationManager.IMPORTANCE_DEFAULT
        const val NOTIFICATION_CHANNEL_DESCRIPTION = "Notifications for appointments"
    }

    private val alarmManager =
        context.getSystemService(AlarmManager::class.java)

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

    fun schedule(appointment: AppointmentNotificationData) {
        if (appointment.status == AppointmentStatusType.CANCELLED) {
            cancel(appointment)
            return
        }

        val notificationTime = appointment.reminderNotificationTime()
        if (notificationTime.isAfter(Instant.now())) {
            setupNotification(
                notificationTime,
                appointment.id,
                AppointmentNotificationType.REMINDER
            )
        }

        val trackingTime = appointment.trackingNotificationTime()
        if (trackingTime.isAfter(Instant.now())) {
            setupNotification(
                notificationTime,
                appointment.id,
                AppointmentNotificationType.TRACKING,
            )
        }
    }
    fun cancel(appointment: AppointmentNotificationData) {
        Log.d("Notification", "Canceling notification for $appointment")

        cancelNotification(appointment.id, AppointmentNotificationType.REMINDER)
        cancelNotification(appointment.id, AppointmentNotificationType.TRACKING)
    }

    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    fun setupNotification(
        time: Instant,
        id: String,
        type: AppointmentNotificationType
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
            && !alarmManager.canScheduleExactAlarms()) {
            Log.w("Notification", "Exact alarm permission unavailable")
            return
        }

        val intent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        ).apply {
            putExtra("id", id)
            putExtra("type",          type)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            31 * type.hashCode() + id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            time.toEpochMilli(),
            pendingIntent
        )
    }
    fun cancelNotification(
        id: String,
        type: AppointmentNotificationType
    ) {
        val intent = Intent(
            context,
            AppointmentNotificationReceiver::class.java
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            31 * type.hashCode() + id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}

