package de.kniederelz.pawplan.tracking.repositories.reports.data

import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.core.extensions.toLocalDateTime
import de.kniederelz.pawplan.core.extensions.toTimestamp
import de.kniederelz.pawplan.tracking.repositories.reports.domain.IncidentReport
import kotlinx.serialization.json.Json

fun FirestoreIncidentReportDto.toDomain(id: String) = IncidentReport(
    id = id,

    appointmentId = appointmentId,
    sessionId = sessionId,

    location = Json.decodeFromString(location),
    description = description,

    reportedBy = reportedBy,
    reportedAt = reportedAt.toLocalDateTime(),
)

fun IncidentReport.toDto() = FirestoreIncidentReportDto(
    appointmentId = appointmentId,
    sessionId = sessionId,

    location = Json.encodeToString(location),
    description = description,

    reportedBy = reportedBy,
    reportedAt = reportedAt.toTimestamp(),
)
