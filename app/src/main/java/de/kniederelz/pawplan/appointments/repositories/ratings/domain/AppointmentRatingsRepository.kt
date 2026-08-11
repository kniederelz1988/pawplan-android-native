package de.kniederelz.pawplan.appointments.repositories.ratings.domain

import androidx.paging.PagingData
import de.kniederelz.pawplan.core.RepositorySubscription
import kotlinx.coroutines.flow.Flow

interface AppointmentRatingRepository {
    suspend fun createRating(rating: AppointmentRating): Result<String>
    suspend fun updateRating(rating: AppointmentRating): Result<String>

    fun observeRating(appointmentId: String): Flow<AppointmentRating?>
    fun observeRatings(dogId: String): Flow<PagingData<AppointmentRating>>

    val dogStatistics: Flow<Map<String, AppointmentRatingStatistics>>
    fun requestDogStatistics(dogId: String): Unit

    fun createSubscription(): RepositorySubscription<AppointmentRating>
}