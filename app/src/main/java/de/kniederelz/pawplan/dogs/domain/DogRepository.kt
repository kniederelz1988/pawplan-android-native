package de.kniederelz.pawplan.dogs.domain

import androidx.paging.PagingData
import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow

interface DogRepository {
    fun observeDog(dogId: String): Flow<Dog?>
    fun observeDogs(): Flow<PagingData<Dog>>

    fun getDogsDataSource(dogIds: List<String>): PagingSource<*, Dog>

    fun createSubscription(): DogSubscription
}