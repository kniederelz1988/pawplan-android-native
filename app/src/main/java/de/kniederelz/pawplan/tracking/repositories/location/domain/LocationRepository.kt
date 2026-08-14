package de.kniederelz.pawplan.tracking.repositories.location.domain

import android.location.Location
import de.kniederelz.pawplan.tracking.repositories.base.domain.LatLngTime
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val location: Flow<LatLngTime>
}