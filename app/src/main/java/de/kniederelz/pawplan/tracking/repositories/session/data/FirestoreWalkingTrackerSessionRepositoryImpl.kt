package de.kniederelz.pawplan.tracking.repositories.session.data

import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSessionRepository
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirestoreWalkingTrackerSessionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): WalkingTrackerSessionRepository {
    companion object {
        const val COLLECTION = "appointmentsSessions"
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

    override fun observeSession(appointmentId: String): Flow<WalkingTrackerSession?> = callbackFlow {
        val registration = firestore
            .collection(COLLECTION)
            .document(appointmentId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (!snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val session = snapshot.toObject(FirestoreWalkingTrackerSessionDto::class.java)
                    ?.toDomain(snapshot.id)

                trySend(session)
            }

            awaitClose {
                registration.remove()
            }
    }
    override fun observeSessions(appointmentIds: List<String>): Flow<Map<String, WalkingTrackerSession>> {
        return combine(
            appointmentIds.map { observeSession(it) }
        ) { sessions ->
            sessions.filterNotNull().associateBy { it.id }
        }
    }
}