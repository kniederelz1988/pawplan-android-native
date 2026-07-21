package de.kniederelz.pawplan.dogs.representation.overview

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
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
class DogsOverviewViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val dogs: Flow<PagingData<Dog>> =
        dogRepository
            .getOverview()
            .cachedIn(viewModelScope)
            .combine(userRepository.userFavorites) { pagingData, favorites ->
                pagingData.map { dog ->
                    dog.copy(isFavorite = favorites.contains(dog))
                }
            }

    fun toggleDogFavorite(dog: Dog) {
        val userFavorites = userRepository.userFavorites.value

        val userFavorite = userFavorites.get(dog)
        Log.d("DogsOverviewViewModel", "userFavorite: $userFavorite")
        if (userFavorite != null) {
            viewModelScope.launch {
                userRepository.deleteFavorite(userFavorite)
                    .onSuccess { Log.d("DogsOverviewViewModel", "Favorites deleted successfully") }
                    .onFailure { Log.d("DogsOverviewViewModel", "Failed to delete favorite", it) }
            }
        } else {
            viewModelScope.launch {
                val user = userRepository.userProfile.value ?: return@launch
                userRepository.createFavorite(user, dog)
                    .onSuccess { Log.d("DogsOverviewViewModel", "Favorites created successfully") }
                    .onFailure { Log.d("DogsOverviewViewModel", "Failed to create favorite", it) }
            }
        }
    }
}