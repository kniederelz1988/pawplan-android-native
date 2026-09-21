package de.kniederelz.pawplan.appointments.notifications

internal data class AppointmentNotificationSyncPlan(
    val notificationsToCancel: List<AppointmentNotificationData>,
    val notificationsToSchedule: List<AppointmentNotificationData>
)

internal fun planAppointmentNotificationSync(
    previous: AppointmentNotificationMap,
    current: AppointmentNotificationMap
): AppointmentNotificationSyncPlan = AppointmentNotificationSyncPlan(
    notificationsToCancel = previous.values.filter { previousAppointment ->
        current[previousAppointment.id] != previousAppointment
    },
    notificationsToSchedule = current.values.filter { currentAppointment ->
        previous[currentAppointment.id] != currentAppointment
    }
)