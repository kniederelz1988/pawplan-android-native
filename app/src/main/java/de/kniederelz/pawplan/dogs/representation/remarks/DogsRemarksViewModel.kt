package de.kniederelz.pawplan.dogs.representation.remarks

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class DogsRemarksViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository,
    private val appointmentRatingRepository: AppointmentRatingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    private val dogId: String =
        checkNotNull(savedStateHandle["dogId"])

    val dog: Flow<Dog?> = dogRepository.observeDog(dogId)

    val ratings: Flow<PagingData<AppointmentRating>> = appointmentRatingRepository.observeRatings(dogId)
        .mapLatest { pagingData ->
            pagingData.map { rating ->
                val volunteerName = userRepository.getProfileName(rating.volunteerId)
                rating.copy(
                    volunteerName = volunteerName
                )
            }
        }
        .onEach {
            Log.d("DogsRemarksViewModel", "Ratings: ${it.map { rating -> rating.comment }}")
        }
}