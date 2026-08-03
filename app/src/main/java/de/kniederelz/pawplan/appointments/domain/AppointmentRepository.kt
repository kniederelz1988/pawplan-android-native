package de.kniederelz.pawplan.appointments.domain

import androidx.paging.PagingData
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRatingStatistics
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    suspend fun createRating(rating: AppointmentRating)

    fun getRating(appointmentId: String): Flow<AppointmentRating?>

    fun getOverview(dogId: String): Flow<PagingData<AppointmentRating>>
    fun getStatistics(dogId: String): Flow<AppointmentRatingStatistics>
}