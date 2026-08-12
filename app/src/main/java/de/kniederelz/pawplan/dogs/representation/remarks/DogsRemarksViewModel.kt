package de.kniederelz.pawplan.dogs.representation.remarks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
class DogsRemarksViewModel @Inject constructor(
    private val dogRepository: DogRepository,
    private val userRepository: UserRepository,
    private val appointmentRatingRepository: AppointmentRatingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel(){

    val dog = dogRepository.observeDog(checkNotNull(savedStateHandle["dogId"]))
        .asLiveData()

    val ratings = appointmentRatingRepository.observeDogRatings(checkNotNull(savedStateHandle["dogId"]))
        .flatMapLatest { ratings ->
            userRepository.observeProfiles(ratings.map { it.volunteerId })
                .mapLatest { userProfiles ->
                    ratings.filter { userProfiles.containsKey(it.volunteerId) }
                        .map { rating ->
                            val userProfile = userProfiles[rating.volunteerId]!!

                            AppointmentRatingData(
                                rating,
                                userProfile
                            )
                        }
                }
            }
        .asLiveData()
}

