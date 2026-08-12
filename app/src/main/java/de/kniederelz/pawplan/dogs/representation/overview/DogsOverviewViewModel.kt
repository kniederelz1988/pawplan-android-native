package de.kniederelz.pawplan.dogs.representation.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.dogs.domain.DogSizeType
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.contains
import de.kniederelz.pawplan.user.domain.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class DogsOverviewViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository,
    private val appointmentRatingRepository: AppointmentRatingRepository
) : ViewModel() {

    private val _userProfileFlow = userRepository.userProfile
    private val _userFavoritesFlow = userRepository.userFavorites

    private val _dogOverviewFilter = MutableStateFlow(DogOverviewFilter())
    val overviewDogFilter = _dogOverviewFilter.asLiveData()

    val overviewDogs = dogRepository.observeAdoptableDogs()
        .flatMapLatest { dogs ->
            combine(
                appointmentRatingRepository.observeDogStatistics(dogs.values.map { it.id }),
                _userFavoritesFlow,
                _dogOverviewFilter
            ) { dogStatistics, userFavorites, filter ->
                filter.apply(dogs.values, userFavorites)
                    .map { dog ->
                        val dogStatistic = dogStatistics[dog.id]

                        DogOverviewData(
                            dog,
                            dogStatistic,
                            userFavorites.contains(dog)
                        )
                    }
            }
        }
        .asLiveData()

    suspend fun toggleDogFavorite(dog: Dog) {
        val userProfile = _userProfileFlow.value
            ?: return
        val userFavorites = _userFavoritesFlow.value

        val userFavorite = userFavorites.get(dog)
        if (userFavorite != null) {
            userRepository.deleteFavorite(userFavorite)
                .onSuccess { Log.d("DogsOverviewViewModel", "Favorites deleted successfully") }
                .onFailure { Log.d("DogsOverviewViewModel", "Failed to delete favorite", it) }
        } else {
            userRepository.createFavorite(userProfile, dog)
                .onSuccess { Log.d("DogsOverviewViewModel", "Favorites created successfully") }
                .onFailure { Log.d("DogsOverviewViewModel", "Failed to create favorite", it) }
        }
    }

    fun filterDogsByName(nameFilter: String) {
        _dogOverviewFilter.update { filter -> filter.copy( filterByName = nameFilter ) }
    }
    fun filterDogsByFavs(favFilter: Boolean) {
        _dogOverviewFilter.update { filter -> filter.copy( filterByFavorites = favFilter ) }
    }
    fun filterDogsBySize(size: DogSizeType, selected: Boolean) {
        _dogOverviewFilter.update { filter ->
            filter.copy(
                filterBySize =
                    if (selected) {
                        filter.filterBySize + size
                    } else {
                        filter.filterBySize - size
                    }
            )
        }
    }
}

