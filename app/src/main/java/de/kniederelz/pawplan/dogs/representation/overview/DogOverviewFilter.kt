package de.kniederelz.pawplan.dogs.representation.overview

import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogSizeType
import de.kniederelz.pawplan.user.domain.UserFavorites
import de.kniederelz.pawplan.user.domain.contains

data class DogOverviewFilter(
    val filterByName: String = "",
    val filterByFavorites: Boolean = false,
    val filterBySize: Set<DogSizeType> = setOf(DogSizeType.SMALL, DogSizeType.MEDIUM, DogSizeType.LARGE)
) {
    fun apply(dogs: Collection<Dog>, userFavorites: UserFavorites): Collection<Dog> {
        return dogs
            .filter { dog -> // name filter
                filterByName.isEmpty() || dog.name.contains(filterByName)
            }
            .filter { dog -> // fav filter
                !filterByFavorites || userFavorites.contains(dog)
            }
            .filter { dog -> // size filter
                filterBySize.contains(dog.size)
            }
    }
}