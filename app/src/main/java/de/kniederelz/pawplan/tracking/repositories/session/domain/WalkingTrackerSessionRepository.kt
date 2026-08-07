package de.kniederelz.pawplan.tracking.repositories.session.domain

interface WalkingTrackerSessionRepository {

    suspend fun createSession(session: WalkingTrackerSession): Result<WalkingTrackerSession>
    suspend fun updateSession(session: WalkingTrackerSession): Result<Unit>

    fun createSubscription(): WalkingTrackerSessionSubscription
}