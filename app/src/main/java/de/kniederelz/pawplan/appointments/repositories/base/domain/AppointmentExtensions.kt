package de.kniederelz.pawplan.appointments.repositories.base.domain

import de.kniederelz.pawplan.core.extensions.checkAfterAndBeforeNow

fun Appointment.canStart(): Boolean {
    return true

    @Suppress("KotlinUnreachableCode")
    return date.checkAfterAndBeforeNow(10, 10)
}