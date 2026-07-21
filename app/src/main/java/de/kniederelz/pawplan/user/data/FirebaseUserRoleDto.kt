package de.kniederelz.pawplan.user.data

import de.kniederelz.pawplan.user.domain.UserRole
import de.kniederelz.pawplan.user.domain.UserRole.Companion.fromValue

data class FirebaseUserRoleDto (
    val role: String = ""
)

fun FirebaseUserRoleDto.toDomain(): UserRole = fromValue(role)