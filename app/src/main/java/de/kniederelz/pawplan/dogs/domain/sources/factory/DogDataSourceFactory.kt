package de.kniederelz.pawplan.dogs.domain.sources.factory

import androidx.paging.PagingSource
import de.kniederelz.pawplan.dogs.domain.Dog

interface DogDataSourceFactory {
    fun createPagingSource(): PagingSource<*, Dog>
}