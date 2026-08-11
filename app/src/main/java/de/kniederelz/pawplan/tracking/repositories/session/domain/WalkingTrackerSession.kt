package de.kniederelz.pawplan.tracking.repositories.session.domain

import de.kniederelz.pawplan.tracking.repositories.base.domain.LatLngTime
import java.time.LocalDateTime

data class WalkingTrackerSession(
    val id: String = "",

    val appointmentId: String = "",

    val startTimestamp: LocalDateTime = LocalDateTime.now(),
    val endTimestamp: LocalDateTime = LocalDateTime.now(),

    val locations: List<LatLngTime> = listOf()
)

