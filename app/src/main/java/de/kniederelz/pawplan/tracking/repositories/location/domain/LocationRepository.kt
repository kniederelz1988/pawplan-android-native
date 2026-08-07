package de.kniederelz.pawplan.tracking.repositories.location.domain

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val location: Flow<Location>
}