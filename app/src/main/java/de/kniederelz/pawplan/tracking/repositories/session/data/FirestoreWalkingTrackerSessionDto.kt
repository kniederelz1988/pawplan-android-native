package de.kniederelz.pawplan.tracking.repositories.session.data

import com.google.firebase.Timestamp

data class FirestoreWalkingTrackerSessionDto(
    val startTimestamp: Timestamp = Timestamp.now(),
    val endTimestamp: Timestamp = Timestamp.now(),

    val locations: String = ""
)
