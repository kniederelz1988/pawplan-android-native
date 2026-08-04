package de.kniederelz.pawplan.appointments.presentation

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class AppointmentsOverviewViewModel @Inject constructor(
    userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val dogRepository: DogRepository
) : ViewModel() {

    val appointments = userRepository.userProfile
        .flatMapLatest { userProfile ->
            userProfile?.let {
                Pager(
                    config = PagingConfig(
                        pageSize = 5,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        appointmentRepository.getAppointmentDataSource(userProfile.id)
                    }
                ).flow
            } ?: flowOf(PagingData.empty())
        }

    val favoritedDogs = userRepository.userFavorites
        .flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = 5,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    dogRepository.getDogsDataSource(it.favorites.map { dog -> dog.dogId }.distinct())
                }
            ).flow
        }

}