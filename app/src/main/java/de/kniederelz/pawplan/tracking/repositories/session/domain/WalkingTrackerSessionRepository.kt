package de.kniederelz.pawplan.tracking.repositories.session.domain

import kotlinx.coroutines.flow.Flow

interface WalkingTrackerSessionRepository {
    suspend fun updateSession(session: WalkingTrackerSession): Result<Unit>

    fun observeSession(appointmentId: String): Flow<WalkingTrackerSession?>
    fun observeSessions(appointmentIds: List<String>): Flow<Map<String, WalkingTrackerSession>>
}