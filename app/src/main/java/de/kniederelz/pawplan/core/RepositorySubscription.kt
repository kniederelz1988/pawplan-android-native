package de.kniederelz.pawplan.core

import kotlinx.coroutines.flow.Flow

abstract class RepositorySubscription<T> {
    abstract val values: Flow<Map<String, T>>

    abstract fun registerListener(id: String): RepositorySubscription<T>
    abstract fun deregisterListener(id: String): RepositorySubscription<T>

    final fun registerBatchListener(ids: List<String>): RepositorySubscription<T> {
        ids.forEach { registerListener(it) }
        return this
    }
    final fun deregisterBatchListener(ids: List<String>): RepositorySubscription<T> {
        ids.forEach { deregisterListener(it) }
        return this
    }

    abstract fun close()
}