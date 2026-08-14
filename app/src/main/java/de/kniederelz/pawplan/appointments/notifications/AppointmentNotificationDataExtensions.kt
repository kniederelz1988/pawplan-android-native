package de.kniederelz.pawplan.appointments.notifications

import java.time.Instant
import java.time.temporal.ChronoUnit

fun AppointmentNotificationData.reminderNotificationTime(): Instant =
    time.minus(1, ChronoUnit.MINUTES)

fun AppointmentNotificationData.trackingNotificationTime(): Instant =
    time