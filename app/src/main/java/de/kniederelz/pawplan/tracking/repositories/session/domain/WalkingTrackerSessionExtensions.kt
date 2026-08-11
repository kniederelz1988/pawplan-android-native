package de.kniederelz.pawplan.tracking.repositories.session.domain

import android.location.Location

fun WalkingTrackerSession.getDistance(): Float {
    if (locations.size < 2) return 0f

    var total = 0f
    val result = FloatArray(1)

    for (i in 0 until locations.lastIndex) {
        val start = locations[i]
        val end = locations[i + 1]

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