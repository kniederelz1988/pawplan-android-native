package de.kniederelz.pawplan.dogs.domain

import kotlinx.coroutines.flow.Flow

interface DogRepository {
    fun observeDog(dogId: String): Flow<Dog?>
    fun observeDogs(dogIds: List<String>): Flow<Map<String, Dog>>

    fun observeAdoptableDogs(): Flow<Map<String, Dog>>
}