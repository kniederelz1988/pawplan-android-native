package de.kniederelz.pawplan.tracking.repositories.reports.domain

import kotlinx.coroutines.flow.Flow

interface IncidentReportRepository {
    suspend fun createReport(report: IncidentReport): Result<IncidentReport>
    suspend fun updateReport(report: IncidentReport): Result<Unit>

    suspend fun getReportsForAppointment(appointmentId: String): Flow<List<IncidentReport>>
    suspend fun getReportsForSession(sessionId: String): Flow<List<IncidentReport>>
}