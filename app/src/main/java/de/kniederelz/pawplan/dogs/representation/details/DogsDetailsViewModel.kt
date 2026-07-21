package de.kniederelz.pawplan.dogs.representation.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import de.kniederelz.pawplan.user.domain.contains
import de.kniederelz.pawplan.user.domain.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DogsDetailsViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val dogId: String =
        checkNotNull(savedStateHandle["dogId"])

    private val _dog = MutableStateFlow<Dog?>(null)
    val dog: Flow<Dog?> = combine(
        _dog,
        userRepository.userFavorites
    ) { dog, favorites ->
        dog?.copy(isFavorite = favorites.contains(dog))
    }

    init {
        loadDog()
    }

    private fun loadDog() {
        viewModelScope.launch {
            _dog.value = dogRepository.getDog(dogId)
        }
    }


    fun toggleDogFavorite(dog: Dog) {
        val userFavorites = userRepository.userFavorites.value

        val userFavorite = userFavorites.get(dog)
        if (userFavorite != null) {
            viewModelScope.launch {
                userRepository.deleteFavorite(userFavorite)
                    .onSuccess { Log.d("DogsDetailsViewModel", "Favorites deleted successfully") }
                    .onFailure { Log.d("DogsDetailsViewModel", "Failed to delete favorite", it) }
            }
        } else {
            viewModelScope.launch {
                val user = userRepository.userProfile.value ?: return@launch
                userRepository.createFavorite(user, dog)
                    .onSuccess { Log.d("DogsDetailsViewModel", "Favorites created successfully") }
                    .onFailure { Log.d("DogsDetailsViewModel", "Failed to create favorite", it) }
            }
        }
    }
}