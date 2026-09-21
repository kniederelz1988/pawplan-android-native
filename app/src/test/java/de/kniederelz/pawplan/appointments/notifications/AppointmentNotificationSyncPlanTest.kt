package de.kniederelz.pawplan.appointments.notifications

import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class AppointmentNotificationSyncPlanTest {

    @Test
    fun `new appointment is scheduled`() {
        val appointment = notification("appointment-1", "2026-09-24T10:00:00Z")

        val plan = planAppointmentNotificationSync(
            previous = emptyMap(),
            current = mapOf(appointment.id to appointment)
        )

        assertEquals(emptyList<AppointmentNotificationData>(), plan.notificationsToCancel)
        assertEquals(listOf(appointment), plan.notificationsToSchedule)
    }

    @Test
    fun `changed appointment is cancelled and rescheduled`() {
        val previous = notification("appointment-1", "2026-09-24T10:00:00Z")
        val changed = notification("appointment-1", "2026-09-24T11:00:00Z")

        val plan = planAppointmentNotificationSync(
            previous = mapOf(previous.id to previous),
            current = mapOf(changed.id to changed)
        )

        assertEquals(listOf(previous), plan.notificationsToCancel)
        assertEquals(listOf(changed), plan.notificationsToSchedule)
    }

    @Test
    fun `removed appointment is cancelled`() {
        val appointment = notification("appointment-1", "2026-09-24T10:00:00Z")

        val plan = planAppointmentNotificationSync(
            previous = mapOf(appointment.id to appointment),
            current = emptyMap()
        )

        assertEquals(listOf(appointment), plan.notificationsToCancel)
        assertEquals(emptyList<AppointmentNotificationData>(), plan.notificationsToSchedule)
    }

    @Test
    fun `unchanged appointment requires no action`() {
        val appointment = notification("appointment-1", "2026-09-24T10:00:00Z")
        val appointments = mapOf(appointment.id to appointment)

        val plan = planAppointmentNotificationSync(
            previous = appointments,
            current = appointments
        )

        assertEquals(emptyList<AppointmentNotificationData>(), plan.notificationsToCancel)
        assertEquals(emptyList<AppointmentNotificationData>(), plan.notificationsToSchedule)
    }

    private fun notification(id: String, time: String) =
        AppointmentNotificationData(
            id = id,
            time = Instant.parse(time),
            status = AppointmentStatusType.CONFIRMED
        )
}