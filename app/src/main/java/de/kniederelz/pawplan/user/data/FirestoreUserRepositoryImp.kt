package de.kniederelz.pawplan.user.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.kniederelz.pawplan.auth.domain.User
import de.kniederelz.pawplan.core.extensions.toTimestamp
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.user.domain.UserFavorite
import de.kniederelz.pawplan.user.domain.UserFavorites
import de.kniederelz.pawplan.user.domain.UserProfile
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

class FirestoreUserRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : UserRepository {
    companion object {
        private const val VOLUNTEERS_COLLECTION = "volunteers2"
        private const val VOLUNTEERS_ROLE_COLLECTION = "volunteerRoles"
        private const val FAVORITES_COLLECTION = "volunteerDogLikes"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    private val _userProfile = _user
        .flatMapLatest { user ->
            user?.let { getProfile(it.uid) }
                ?: flowOf(UserProfile())
        }
        .onEach { Log.d("FirestoreUserRepository", "Profile: $it") }
        .stateIn(scope,SharingStarted.WhileSubscribed(5000), null)
    override val userProfile: StateFlow<UserProfile?> = _userProfile

    private val _userRole = _userProfile
        .flatMapLatest { userProfile ->
            userProfile?.let { getRole(it.id) }
                ?: flowOf(UserRole.OBSERVER)
        }
        .onEach { Log.d("FirestoreUserRepository", "Role: $it") }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000),UserRole.OBSERVER)
    override val userRole: StateFlow<UserRole> = _userRole

    private val _userFavorites = _userProfile
        .flatMapLatest { userProfile ->
            userProfile?.let { getFavorites(it.id)}
                ?: flowOf(UserFavorites())
        }
        .onEach { Log.d("FirestoreUserRepository", "Favs: $it") }
        .stateIn(scope, SharingStarted.WhileSubscribed(5000), UserFavorites())
    override val userFavorites: StateFlow<UserFavorites> = _userFavorites

    private val profileNameCache = mutableMapOf<String, String>()
    override suspend fun getProfileName(volunteerId: String): String {
        if (profileNameCache.contains(volunteerId))
            return profileNameCache[volunteerId] ?: ""

        val snapshot = firestore
            .collection(VOLUNTEERS_COLLECTION)
            .document(volunteerId)
            .get()
            .await()

        val name = snapshot.getString("name") ?: ""
        profileNameCache[volunteerId] = name

        return name
    }

    private fun getProfile(userId: String) = callbackFlow {
        val registration = firestore
            .collection(VOLUNTEERS_COLLECTION)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(UserProfile())
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.size() != 1){
                    trySend(UserProfile())
                    return@addSnapshotListener
                }

                val userProfile = snapshot.documents.firstOrNull()?.let {
                    it.toObject(FirebaseUserProfileDto::class.java)
                        ?.toDomain(it.id)
                } ?: UserProfile()

                val result = trySend(userProfile)
                Log.e("FirestoreUserRepository Profile", "Result: ${result.isSuccess}")
            }

        awaitClose {
            registration.remove()
        }
    }
    override suspend fun updateProfile(userId: String, user: UserProfile): Result<Unit> {
        firestore
            .collection(VOLUNTEERS_COLLECTION)
            .document(user.id)
            .set(FirebaseUserProfileDto(
                userId = userId,
                name = user.name,
                phoneNumber = user.phoneNumber,
                birthday = user.birthday.toTimestamp(),
                volunteerSince = user.volunteerSince.toTimestamp()
            ))
            .await()

        return Result.success(Unit)
    }

    private fun getRole(profileId: String) = callbackFlow {
        val registration = firestore
            .collection(VOLUNTEERS_ROLE_COLLECTION)
            .document(profileId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(UserRole.OBSERVER)
                    return@addSnapshotListener
                }

                if (snapshot == null) {
                    trySend(UserRole.OBSERVER)
                    return@addSnapshotListener
                }

                val userRoleDto = snapshot.toObject(FirebaseUserRoleDto::class.java)
                Log.e("FirestoreUserRepository Role", "Result: $userRoleDto")

                val userRole = userRoleDto?.toDomain() ?: UserRole.OBSERVER

                val result = trySend(userRole)
                Log.e("FirestoreUserRepository Role", "Result: ${result.isSuccess} -> $userRole")
            }

        awaitClose {
            registration.remove()
        }
    }
    private fun getFavorites(profileId: String) = callbackFlow {
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

    override suspend fun createFavorite(user: UserProfile, dog: Dog): Result<Unit> {
        firestore
            .collection(FAVORITES_COLLECTION)
            .add(FirebaseUserFavoriteDto(dog.id, user.id))
            .await()

        return Result.success(Unit)
    }
    override suspend fun deleteFavorite(fav: UserFavorite): Result<Unit> {
        if (fav.id == null)
            return Result.failure(IllegalArgumentException("Favorite has no ID"))

        firestore
            .collection(FAVORITES_COLLECTION)
            .document(fav.id)
            .delete()
            .await()

        return Result.success(Unit)
    }
}