package de.kniederelz.pawplan.appointments.repositories.ratings.domain.sources.factories

import androidx.paging.PagingSource
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating

interface AppointmentRatingDataSourceFactory {
    fun createPagingSource(dogId: String): PagingSource<*, AppointmentRating>
}