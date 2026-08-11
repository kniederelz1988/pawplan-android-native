package de.kniederelz.pawplan.tracking.services

import android.util.Log
import androidx.lifecycle.lifecycleScope
import de.kniederelz.pawplan.core.extensions.toLong
import de.kniederelz.pawplan.tracking.repositories.base.domain.LatLngTime
import de.kniederelz.pawplan.tracking.repositories.session.domain.WalkingTrackerSession
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.StringJoiner
import java.time.Duration

class WalkingTracker(
    private val service: WalkingTrackerService,
    private var session: WalkingTrackerSession
) {
    init {
        service.lifecycleScope.launch {
            service.locationRepository.location.collect { location ->
                if (!service.trackingStateHolder.isActive())
                    return@collect

                val latLngTime = LatLngTime(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = LocalDateTime.now().toLong()
                )
                session = session.copy(locations = session.locations.plusElement(latLngTime))
                session = session.copy(endTimestamp = LocalDateTime.now())
                updateSession(WalkingTrackerState.Started)
            }
        }
    }

    fun startTracking() {
        if (service.trackingStateHolder.isActive()) return

        session = session.copy(startTimestamp = LocalDateTime.now())
        updateSession(WalkingTrackerState.Started)
    }
    fun stopTracking() {
        if (!service.trackingStateHolder.isActive()) return

        session = session.copy(endTimestamp = LocalDateTime.now())
        updateSession(WalkingTrackerState.Stopped)
    }

    private fun updateSession(state: WalkingTrackerState) {
        service.trackingStateHolder.updateState(state)
        service.trackingStateHolder.updateSession(session)

        service.lifecycleScope.launch {
            service.walkingTrackerSessionRepository.updateSession(session)
                .onSuccess {
                    Log.d("WalkingTracker", "Session updated ${session.id}")
                }
                .onFailure {
                    Log.e("WalkingTracker", "Session update failed", it)
                }
        }
        printTrackingResult()
    }

    private fun printTrackingResult() {
        val positionStr = StringJoiner(",\n", "[", "]")
        session.locations.forEach { positionStr.add("(${it.latitude}, ${it.longitude} -> ${it.timestamp})") }

        val duration = Duration.between(session.startTimestamp, if (service.trackingStateHolder.isActive()) LocalDateTime.now() else session.endTimestamp)
        Log.i(
            "WalkingTracker",
            "Session [" +
                "Active: ${service.trackingStateHolder.isActive()} " +
                "Duration: ${(duration.toHours())}h ${(duration.toMinutes() % 60)}m " +
                "Positions: $positionStr" +
            "]"
        )
    }
}
