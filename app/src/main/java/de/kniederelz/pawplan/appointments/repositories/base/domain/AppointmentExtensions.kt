package de.kniederelz.pawplan.appointments.repositories.base.domain

import de.kniederelz.pawplan.core.extensions.checkAfterAndBeforeNow
import de.kniederelz.pawplan.core.extensions.checkAfterNow

fun Appointment.canStart(): Boolean {
    return date.checkAfterAndBeforeNow(10, 10)
}
fun Appointment.canComplete(): Boolean {
    return !date.checkAfterNow(10)
}