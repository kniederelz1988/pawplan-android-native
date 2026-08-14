package de.kniederelz.pawplan.tracking.repositories.reports.data

import com.google.firebase.Timestamp

data class FirestoreIncidentReportDto(
    val sessionId: String = "",

    val location: String = "",
    val description: String = "",

    val reportedBy: String = "",
    val reportedAt: Timestamp = Timestamp.now()
)