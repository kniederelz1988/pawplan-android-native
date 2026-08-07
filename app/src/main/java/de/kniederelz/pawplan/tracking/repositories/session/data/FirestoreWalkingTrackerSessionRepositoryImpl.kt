package de.kniederelz.pawplan.tracking.repositories.session.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionSubscription
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreWalkingTrackerSessionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WalkingTrackerSessionRepository {
    companion object {
        const val COLLECTION = "walkingtrackerSessions"
    }

    override suspend fun createSession(session: WalkingTrackerSession): Result<WalkingTrackerSession> {
        return withContext(NonCancellable) {
            try {
                val result = firestore
                    .collection(COLLECTION)
                    .add(session.toDto())
                    .await()

                Result.success(session.copy(id = result.id))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override suspend fun updateSession(session: WalkingTrackerSession): Result<Unit> {
        return withContext(NonCancellable) {
            try {
                if (session.id.isEmpty())
                    throw IllegalArgumentException("Session ID cannot be blank")

                firestore
                    .collection(COLLECTION)
                    .document(session.id)
                    .set(session.toDto())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override fun createSubscription(): WalkingTrackerSessionSubscription {
        return FirestoreWalkingTrackerSessionSubscriptionImpl(firestore)
    }
}