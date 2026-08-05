package de.kniederelz.pawplan.core.extensions

import android.util.Log
import com.google.firebase.Timestamp
import java.time.LocalDate
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

fun LocalDateTime.isAfterStartOfDay() : Boolean {
    Log.d("LocalDateTime", "current: $this, startOfDay: ${LocalDate.now().atStartOfDay()}")
    return isAfter(LocalDate.now().atStartOfDay())
}