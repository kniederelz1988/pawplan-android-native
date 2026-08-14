package de.kniederelz.pawplan.core.extensions

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.Instant

fun Timestamp.toLocalDateTime() : LocalDateTime {
    return this.toDate().toInstant().atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun LocalDateTime.toTimestamp() : Timestamp {
    val instant = atZone(ZoneId.systemDefault()).toInstant()
    return Timestamp(instant.epochSecond, instant.nano)
}

fun LocalDateTime.toLong(): Long {
    return atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()
}
fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

fun LocalDateTime.isAfterStartOfDay() : Boolean {
    return isAfter(LocalDate.now().atStartOfDay())
}
fun LocalDateTime.isBeforeStartOfDay() : Boolean {
    return isBefore(LocalDate.now().atStartOfDay())
}

fun LocalDateTime.checkAfterAndBeforeNow(minutesBefore: Long, minutesAfter: Long): Boolean {
    return isAfter(LocalDateTime.now().minusMinutes(minutesBefore)) &&
            isBefore(LocalDateTime.now().plusMinutes(minutesAfter))
}
fun LocalDateTime.checkAfterNow(minutesBefore: Long): Boolean {
    return isAfter(LocalDateTime.now().minusMinutes(minutesBefore))
}