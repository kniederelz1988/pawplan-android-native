package de.kniederelz.pawplan.user.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.user.data.extensions.toDomain
import de.kniederelz.pawplan.user.data.extensions.toDto
import de.kniederelz.pawplan.user.domain.UserFavorite
import de.kniederelz.pawplan.user.domain.UserFavorites
import de.kniederelz.pawplan.user.domain.UserProfile
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.UserRole
import de.kniederelz.pawplan.user.domain.UserRoleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirestoreUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {
    companion object {
        private const val VOLUNTEERS_COLLECTION = "volunteers2"
        private const val VOLUNTEERS_ROLE_COLLECTION = "volunteerRoles"
        private const val FAVORITES_COLLECTION = "volunteerDogLikes"
    }

    private val _user = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)

        trySend(firebaseAuth.currentUser)

        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override val userProfile = _user
        .flatMapLatest { user -> observeProfile(user) }
        .stateIn(scope, SharingStarted.Eagerly, null)

    override val userRole = userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null)
                return@flatMapLatest flowOf(UserRoleType.OBSERVER)

            observeRole(userProfile.id)
        }
        .stateIn(scope, SharingStarted.Eagerly, UserRoleType.OBSERVER)
    override val userFavorites = userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null)
                return@flatMapLatest flowOf(UserFavorites())

            observeFavorites(userProfile.id)
        }
        .stateIn(scope, SharingStarted.Eagerly, UserFavorites())

    private fun observeProfile(user: FirebaseUser?) = callbackFlow {
        if (user == null) {
            trySend(null)

            awaitClose {  }
            return@callbackFlow
        }

        val registration = firestore
            .collection(VOLUNTEERS_COLLECTION)
            .whereEqualTo("userId", user.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot.size() != 1){
                    trySend(null)
                    return@addSnapshotListener
                }

                val userProfile = snapshot.documents.firstOrNull()?.let {
                    it.toObject(FirebaseUserProfileDto::class.java)
                        ?.toDomain(it.id)
                }

                trySend(userProfile)
            }

        awaitClose {
            registration.remove()
        }
    }

    override suspend fun observeProfiles(profileIds: List<String>): Flow<Map<String, UserProfile>> {
        return combine(
            profileIds.map { profileId -> observeProfile(profileId) }
        ) { userProfiles ->
            userProfiles.associateBy { it.id }
        }
    }
    override suspend fun observeProfile(profileId: String) = callbackFlow {
        if (profileId.isEmpty()) {
            trySend(UserProfile())

            awaitClose {  }
            return@callbackFlow
        }
        val registration = firestore
            .collection(VOLUNTEERS_COLLECTION)
            .document(profileId)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) {
                    close(exception)
                    return@addSnapshotListener
                }

                val userProfile = snapshot.toObject(FirebaseUserProfileDto::class.java)
                    ?.toDomain(snapshot.id) ?: UserProfile()
                trySend(userProfile)
            }

        awaitClose {
            registration.remove()
        }
    }

    private fun observeRole(profileId: String) = callbackFlow {
        if (profileId.isEmpty()) {
            trySend(UserRoleType.OBSERVER)

            awaitClose {  }
            return@callbackFlow
        }

        val registration = firestore
            .collection(VOLUNTEERS_ROLE_COLLECTION)
            .document(profileId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    close(error)
                    return@addSnapshotListener
                }

                val userRole = snapshot.toObject(FirebaseUserRoleDto::class.java)
                    ?.toDomain(snapshot.id)

                trySend(userRole?.role ?: UserRoleType.OBSERVER)
            }

        awaitClose {
            registration.remove()
        }
    }
    private fun observeFavorites(profileId: String) = callbackFlow {
        if (profileId.isEmpty()) {
            trySend(UserFavorites())

            awaitClose {  }
            return@callbackFlow
        }

        val registration = firestore
            .collection(FAVORITES_COLLECTION)
            .whereEqualTo("volunteerId", profileId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.size() == 0){
                    trySend(UserFavorites())
                    return@addSnapshotListener
                }

                val favorites = snapshot.documents.mapNotNull {
                    it.toObject(FirebaseUserFavoriteDto::class.java)
                        ?.toDomain(it.id)
                }

                trySend(UserFavorites(favorites))
            }

        awaitClose {
            registration.remove()
        }
    }

    override suspend fun createProfile(userProfile: UserProfile): Result<String> {
        if (!userProfile.id.isEmpty())
            return Result.failure(IllegalArgumentException("Profile has ID"))

        return withContext(NonCancellable) {
            try {
                val result = firestore
                    .collection(VOLUNTEERS_COLLECTION)
                    .add(userProfile.toDto())
                    .await()

                Result.success(result.id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override suspend fun updateProfile(userProfile: UserProfile): Result<Unit> {
        if (userProfile.id.isEmpty())
            return Result.failure(IllegalArgumentException("Profile has no ID"))

        return withContext(NonCancellable) {
            try {
                firestore
                    .collection(VOLUNTEERS_COLLECTION)
                    .document(userProfile.id)
                    .set(userProfile.toDto())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun createRole(userRole: UserRole): Result<String> {
        if (!userRole.id.isEmpty())
            return Result.failure(IllegalArgumentException("Profile has ID"))

        return withContext(NonCancellable) {
            try {
                val result = firestore
                    .collection(VOLUNTEERS_ROLE_COLLECTION)
                    .add(userRole.toDto())
                    .await()

                Result.success(result.id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override suspend fun updateRole(userRole: UserRole): Result<Unit> {
        if (userRole.id.isEmpty())
            return Result.failure(IllegalArgumentException("Profile has no ID"))

        return withContext(NonCancellable) {
            try {
                val result = firestore
                    .collection(VOLUNTEERS_ROLE_COLLECTION)
                    .document(userRole.id)
                    .set(userRole.toDto())
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    override suspend fun createFavorite(userProfile: UserProfile, dog: Dog): Result<Unit> {
        return withContext(NonCancellable) {
            try {
                firestore
                    .collection(FAVORITES_COLLECTION)
                    .add(FirebaseUserFavoriteDto(dog.id, userProfile.id))
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    override suspend fun deleteFavorite(fav: UserFavorite): Result<Unit> {
        return withContext(NonCancellable) {
            try {
                if (fav.id == null)
                    return@withContext Result.failure(IllegalArgumentException("Favorite has no ID"))

                firestore
                    .collection(FAVORITES_COLLECTION)
                    .document(fav.id)
                    .delete()
                    .await()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}