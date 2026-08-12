package de.kniederelz.pawplan.appointments.repositories.ratings.domain

import androidx.paging.PagingData
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.flow.Flow

interface AppointmentRatingRepository {
    suspend fun createRating(rating: AppointmentRating): Result<String>
    suspend fun updateRating(rating: AppointmentRating): Result<String>

    fun observeRating(appointmentId: String): Flow<AppointmentRating?>
    fun observeRatings(appointmentIds: List<String>): Flow<Map<String, AppointmentRating>>

    fun observeDogRatings(dogId: String): Flow<List<AppointmentRating>>

    fun observeDogStatistics(dogId: String): Flow<AppointmentRatingStatistics?>
    fun observeDogStatistics(dogIds: List<String>): Flow<Map<String, AppointmentRatingStatistics>>
}