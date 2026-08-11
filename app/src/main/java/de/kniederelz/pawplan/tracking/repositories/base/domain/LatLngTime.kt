package de.kniederelz.pawplan.tracking.repositories.base.domain

import kotlinx.serialization.Serializable

@Serializable
data class LatLngTime(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val timestamp: Long = 0L
)