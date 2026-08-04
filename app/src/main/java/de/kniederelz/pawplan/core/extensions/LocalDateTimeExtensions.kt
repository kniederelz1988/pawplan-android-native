package de.kniederelz.pawplan.core.extensions

import com.google.firebase.Timestamp
import java.time.LocalDateTime
import java.time.ZoneId

fun Timestamp.toLocalDateTime() : LocalDateTime {
    return this.toDate().toInstant().atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun LocalDateTime.toTimestamp() : Timestamp {
    val instant = atZone(ZoneId.systemDefault()).toInstant()
    return Timestamp(instant.epochSecond, instant.nano)
}