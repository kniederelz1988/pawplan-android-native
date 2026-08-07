package de.kniederelz.pawplan.tracking.repositories.session.data

import de.kniederelz.pawplan.core.extensions.toLocalDateTime
import de.kniederelz.pawplan.core.extensions.toTimestamp
import kotlinx.serialization.json.Json

import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession

fun FirestoreWalkingTrackerSessionDto.toDomain(id: String) = WalkingTrackerSession(
    id = id,

    startTimestamp = startTimestamp.toLocalDateTime(),
    endTimestamp = endTimestamp.toLocalDateTime(),

    locations = Json.decodeFromString(locations)
)

fun WalkingTrackerSession.toDto() = FirestoreWalkingTrackerSessionDto(
    startTimestamp = startTimestamp.toTimestamp(),
    endTimestamp = endTimestamp.toTimestamp(),

    locations = Json.encodeToString(locations)
)
