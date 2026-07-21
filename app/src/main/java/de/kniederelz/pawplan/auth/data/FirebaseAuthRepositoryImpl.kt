package de.kniederelz.pawplan.auth.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import de.kniederelz.pawplan.auth.domain.AuthRepository
import de.kniederelz.pawplan.auth.domain.User
import de.kniederelz.pawplan.auth.domain.toUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepositoryImpl(private val auth: FirebaseAuth) : AuthRepository {
    private val _currentUser = MutableStateFlow(auth.currentUser?.toUser())
    override val currentUser: StateFlow<User?> = _currentUser

    init {
        auth.addAuthStateListener {
            _currentUser.value = it.currentUser?.toUser()
        }
    }

    override suspend fun signIn(email: String, password: String)
        : Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password)
                .await()

            if (result.user != null) {
                val firebaseUser = result.user!!
                return Result.success(
                    firebaseUser.toUser()
                )
            }

            Result.failure(
                Exception("No user")
            )
        } catch (e: Exception) {
            Result.failure(
                e
            )
        }
    }
    override suspend fun signOut()
        : Result<Unit> {
        auth.signOut()
        return Result.success(Unit)
    }

    override suspend fun register(email: String, password: String, displayName: String)
        : Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password)
                .await()

            if (result.user != null) {
                val firebaseUser = result.user!!

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()

                firebaseUser.updateProfile(profileUpdates)
                    .await()

                return Result.success(firebaseUser.toUser())
            }

            Result.failure(
                Exception("No user")
            )
        } catch (e: Exception) {
            Result.failure(
                e
            )
        }
    }

    override suspend fun updateName(displayName: String)
        : Result<User> {
        return try {
            val firebaseUser = auth.currentUser ?:
                return Result.failure(Exception("No user"))

            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()

            firebaseUser.updateProfile(profileUpdates)
                .await()
            firebaseUser.reload()
                .await()

            _currentUser.value = auth.currentUser?.toUser()
            Result.success(currentUser.value!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}