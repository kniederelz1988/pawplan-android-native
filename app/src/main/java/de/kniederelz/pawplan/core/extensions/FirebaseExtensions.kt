package de.kniederelz.pawplan.core.extensions

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId

fun Timestamp.toLocalDate(): LocalDate {
    return this.toDate()
        .toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}
