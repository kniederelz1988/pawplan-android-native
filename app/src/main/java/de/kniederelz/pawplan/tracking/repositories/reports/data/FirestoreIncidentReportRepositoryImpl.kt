package de.kniederelz.pawplan.tracking.repositories.reports.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.tracking.repositories.reports.domain.IncidentReport
import de.kniederelz.pawplan.tracking.repositories.reports.domain.IncidentReportRepository
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreIncidentReportRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): IncidentReportRepository {
    companion object {
        const val COLLECTION = "incidentReports"
    }

    override suspend fun createReport(report: IncidentReport): Result<IncidentReport> {
        if (report.id.isNotEmpty())
            return Result.failure(IllegalArgumentException("Report ID must be blank"))

        return withContext(NonCancellable) {
            try {
                val result = firestore
                    .collection(COLLECTION)
                    .add(report.toDto())
                    .await()

                Result.success(report.copy(id = result.id))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override suspend fun updateReport(report: IncidentReport): Result<Unit> {
        if (report.id.isEmpty())
            return Result.failure(IllegalArgumentException("Report ID cannot be blank"))

        return withContext(NonCancellable) {
            try {
                firestore
                    .collection(COLLECTION)
                    .document(report.id)
                    .set(report.toDto())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun getReportsForAppointment(appointmentId: String) = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .whereEqualTo("appointmentId", appointmentId)
            .addSnapshotListener { snapshots, exception ->
                if (exception != null || snapshots == null) {
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshots.isEmpty) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val reports = snapshots.documents.mapNotNull {
                    it.toObject(FirestoreIncidentReportDto::class.java)
                        ?.toDomain(it.id)
                }
                trySend(reports)
            }

        awaitClose {
            registration.remove()
        }
    }
    override suspend fun getReportsForSession(sessionId: String): Flow<List<IncidentReport>> {
        return getReportsForAppointment(sessionId)
    }
}