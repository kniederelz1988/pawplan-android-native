package de.kniederelz.pawplan.core.time

import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface ClockProvider {
    val now: Flow<LocalDateTime>
}