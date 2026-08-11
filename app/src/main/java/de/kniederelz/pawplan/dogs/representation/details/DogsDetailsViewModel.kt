package de.kniederelz.pawplan.dogs.representation.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.contains
import de.kniederelz.pawplan.user.domain.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogsDetailsViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository,
    appointmentRatingRepository: AppointmentRatingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dogId: String =
        checkNotNull(savedStateHandle["dogId"])

    val dog: Flow<Dog?> = combine(
        dogRepository.observeDog(dogId),
        appointmentRatingRepository.dogStatistics,
        userRepository.userFavoritesFlow,
    ) { dog, statistics, favorites ->
        dog?.let {
            appointmentRatingRepository.requestDogStatistics(dog.id)
            dog.copy(
                isFavorite = favorites.contains(dog),
                statistics = statistics[dog.id]
            )
        }
    }

    fun toggleDogFavorite(dog: Dog) {
        val userFavorites = userRepository.userFavoritesFlow.value

        val userFavorite = userFavorites.get(dog)
        if (userFavorite != null) {
            viewModelScope.launch {
                userRepository.deleteFavorite(userFavorite)
                    .onSuccess { Log.d("DogsDetailsViewModel", "Favorites deleted successfully") }
                    .onFailure { Log.d("DogsDetailsViewModel", "Failed to delete favorite", it) }
            }
        } else {
            viewModelScope.launch {
                val user = userRepository.userProfileFlow.value ?: return@launch
                userRepository.createFavorite(user, dog)
                    .onSuccess { Log.d("DogsDetailsViewModel", "Favorites created successfully") }
                    .onFailure { Log.d("DogsDetailsViewModel", "Failed to create favorite", it) }
            }
        }
    }
}