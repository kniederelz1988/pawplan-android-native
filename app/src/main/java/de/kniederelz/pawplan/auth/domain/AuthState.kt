package de.kniederelz.pawplan.auth.domain

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null
)