package de.kniederelz.pawplan.dogs.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface DogRepository {
    fun getOverview(): Flow<PagingData<Dog>>

    suspend fun getDog(dogId: String): Dog?
}