package de.kniederelz.pawplan.user.domain

enum class UserRole(val value: String) {
    OBSERVER("observer"),
    VOLUNTEER("volunteer"),
    ADMIN("admin");

    companion object {
        fun fromValue(value: String?): UserRole =
            entries.firstOrNull { it.value == value } ?: UserRole.OBSERVER
    }
}