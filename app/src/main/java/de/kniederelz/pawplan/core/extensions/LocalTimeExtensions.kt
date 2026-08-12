package de.kniederelz.pawplan.core.extensions

import java.time.LocalTime
import java.time.format.DateTimeFormatter

val timeFormatter: DateTimeFormatter? = DateTimeFormatter.ofPattern("HH:mm")

fun LocalTime.roundToFiveMinutes(): LocalTime {
    val roundedMinute = ((minute + 2) / 5) * 5

    return if (roundedMinute == 60) {
        LocalTime.of((hour + 1) % 24, 0)
    } else {
        LocalTime.of(hour, roundedMinute)
    }
}
