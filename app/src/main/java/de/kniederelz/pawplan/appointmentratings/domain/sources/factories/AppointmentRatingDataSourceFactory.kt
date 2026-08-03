package de.kniederelz.pawplan.appointmentratings.domain.sources.factories

import androidx.paging.PagingSource
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRating

interface AppointmentRatingDataSourceFactory {
    fun createPagingSource(dogId: String): PagingSource<*, AppointmentRating>
}