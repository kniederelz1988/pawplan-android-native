package de.kniederelz.pawplan.user.domain

enum class UserRoleType(val value: String) {
    OBSERVER("observer"),
    VOLUNTEER("volunteer"),
    ADMIN("admin");

    companion object {
        fun fromValue(value: String?): UserRoleType =
            entries.firstOrNull { it.value == value } ?: OBSERVER
    }
}