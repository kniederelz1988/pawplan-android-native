package de.kniederelz.pawplan.dogs.representation.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.contains
import de.kniederelz.pawplan.user.domain.get
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogsOverviewViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository,
    private val appointmentRatingRepository: AppointmentRatingRepository
) : ViewModel() {

    private val _userProfileFlow = userRepository.userProfile
    private val _userFavoritesFlow = userRepository.userFavorites

    val overviewDogs = dogRepository.observeAdoptableDogs()
        .flatMapLatest { dogs ->
            combine(
                appointmentRatingRepository.observeDogStatistics(dogs.values.map { it.id }),
                _userFavoritesFlow
            ) { dogStatistics, userFavorites ->
                dogs.values.map { dog ->
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
}