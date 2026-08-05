package de.kniederelz.pawplan.appointments.repositories.status.domain

import kotlinx.coroutines.flow.Flow

interface AppointmentStatusSubscription {
    val values: Flow<Map<String, AppointmentStatus>>

    fun registerStatusListener(appointmentId: String)
    fun deregisterStatusListener(appointmentId: String)

    fun registerBatchListener(appointmentIds: List<String>)
    fun deregisterBatchListener(appointmentIds: List<String>)

    fun close()
}