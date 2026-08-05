package de.kniederelz.pawplan.dogs.domain

import kotlinx.coroutines.flow.Flow

interface DogSubscription {
    val values: Flow<Map<String, Dog>>

    fun registerStatusListener(dogId: String)
    fun deregisterStatusListener(dogId: String)

    fun registerBatchListener(dogIds: List<String>)
    fun deregisterBatchListener(dogIds: List<String>)

    fun close()
}