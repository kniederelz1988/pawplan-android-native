package de.kniederelz.pawplan.core.time

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.minutes

@Singleton
class SystemClockProvider @Inject constructor() : ClockProvider {

    override val now: Flow<LocalDateTime> = flow {
        while (true) {
            emit(LocalDateTime.now())
            delay(1.minutes)
        }
    }.distinctUntilChanged()
}