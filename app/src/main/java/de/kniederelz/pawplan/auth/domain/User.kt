package de.kniederelz.pawplan.auth.domain

import com.google.firebase.auth.FirebaseUser

data class User(
    val uid: String,
    val email: String?,
    val displayName: String?
)

fun FirebaseUser.toUser() = User(
    uid = uid,
    email = email,
    displayName = displayName
)
