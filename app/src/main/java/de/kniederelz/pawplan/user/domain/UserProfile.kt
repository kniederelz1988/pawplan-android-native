package de.kniederelz.pawplan.user.domain

import java.time.LocalDate

data class UserProfile(
    var id: String = "",
    var userId: String = "",

    var name: String = "",
    var phoneNumber: String = "",
    var imageUrl: String = "",

    var birthday: LocalDate = LocalDate.now(),
    var volunteerSince: LocalDate = LocalDate.now(),
)
