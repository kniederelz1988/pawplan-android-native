package de.kniederelz.pawplan.tracking.repositories.session.domain

import android.location.Location
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession.LatLngTime
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

data class WalkingTrackerSession(
    val id: String = "",

    val startTimestamp: LocalDateTime = LocalDateTime.now(),
    val endTimestamp: LocalDateTime = LocalDateTime.now(),

    val locations: List<LatLngTime> = listOf()
) {
    @Serializable
    data class LatLngTime(val latitude: Double, val longitude: Double, val timestamp: Long)
}


fun List<LatLngTime>.getDistance(): Float {
    if (size < 2) return 0f

    var total = 0f
    val result = FloatArray(1)

    for (i in 0 until lastIndex) {
        val start = this[i]
        val end = this[i + 1]

        Location.distanceBetween(
            start.latitude,
            start.longitude,
            end.latitude,
            end.longitude,
            result
        )

        total += result[0]
    }

    return total
}