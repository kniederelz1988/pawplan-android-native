package de.kniederelz.pawplan.core.utils

import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId

class TimestampUtils {
    companion object {
        fun startOfDay(): Timestamp {
            val startOfToday = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()

            return Timestamp(
                startOfToday.epochSecond,
                startOfToday.nano
            )
        }
    }
}