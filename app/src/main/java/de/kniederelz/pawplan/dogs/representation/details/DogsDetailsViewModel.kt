package de.kniederelz.pawplan.dogs.representation.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.contains
import de.kniederelz.pawplan.user.domain.get
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class DogsDetailsViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository,
    appointmentRatingRepository: AppointmentRatingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _userProfileFlow = userRepository.userProfile
    private val _userFavoritesFlow = userRepository.userFavorites

    val dogDetails = dogRepository.observeDog(checkNotNull(savedStateHandle["dogId"]))
        .flatMapLatest { dog ->
            if (dog == null)
                return@flatMapLatest flowOf(null)

            combine(
                appointmentRatingRepository.observeDogStatistics(dog.id),
                _userFavoritesFlow
            ) { statistics, favorites ->
                DogDetailsData(
                    dog,
                    statistics,
                    favorites.contains(dog)
                )
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
                .onSuccess { Log.d("DogsDetailsViewModel", "Favorites deleted successfully") }
                .onFailure { Log.d("DogsDetailsViewModel", "Failed to delete favorite", it) }
        } else {
            userRepository.createFavorite(userProfile, dog)
                .onSuccess { Log.d("DogsDetailsViewModel", "Favorites created successfully") }
                .onFailure { Log.d("DogsDetailsViewModel", "Failed to create favorite", it) }
        }
    }
}