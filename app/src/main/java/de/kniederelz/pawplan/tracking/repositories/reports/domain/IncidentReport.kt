package de.kniederelz.pawplan.tracking.repositories.reports.domain

import de.kniederelz.pawplan.tracking.repositories.base.domain.LatLngTime
import java.time.LocalDateTime

data class IncidentReport(
    val id: String,
    val appointmentId: String,
    val sessionId: String,

    val location: LatLngTime,
    val description: String,

    val reportedBy: String,
    val reportedAt: LocalDateTime
)
