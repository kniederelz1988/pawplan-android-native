package de.kniederelz.pawplan.tracking.repositories.location.domain

import de.kniederelz.pawplan.tracking.repositories.base.domain.LatLngTime
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    val location: Flow<LatLngTime>
}