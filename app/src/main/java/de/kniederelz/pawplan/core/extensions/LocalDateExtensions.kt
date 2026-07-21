package de.kniederelz.pawplan.core.extensions

import com.google.firebase.Timestamp
import de.kniederelz.pawplan.dogs.domain.Dog
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val dateFormatter = DateTimeFormatter.ofPattern("EEE, d MMM yyyy")

fun Long.toLocalDate() : LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

fun LocalDate.toTimestamp() : Timestamp {
    val instant = atStartOfDay(ZoneId.systemDefault()).toInstant()
    return Timestamp(instant.epochSecond, instant.nano)
}