package de.kniederelz.pawplan.appointments.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentData
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatus
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@HiltViewModel
class AppointmentsOverviewViewModel @Inject constructor(
    userRepository: UserRepository,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val dogRepository: DogRepository
) : ViewModel() {

    val appointmentStatusSubscription
            = appointmentStatusRepository.createSubscription()
    val dogSubscription
            = dogRepository.createSubscription()

    private val _appointments = userRepository.userProfile
        .flatMapLatest { userProfile ->
            userProfile?.let {
                Pager(
                    config = PagingConfig(
                        pageSize = 5,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        appointmentRepository.getAppointmentDataSource(
                            userProfile.id,
                            appointmentStatusSubscription,
                            dogSubscription
                        )
                    }
                ).flow
            } ?: flowOf(PagingData.empty())
        }

    val appointments = combine(
            _appointments,
            appointmentStatusSubscription.values,
            dogSubscription.values
        ) { appointments, status, dog ->
            appointments.map { appointment ->
                val appointmentStatus = status[appointment.id] ?: AppointmentStatus.EMPTY
                val dog = dog[appointment.id] ?: Dog.EMPTY

                AppointmentData(
                    appointment.id,
                    appointment,
                    appointmentStatus,
                    dog
                )
            }
        }
        .cachedIn(viewModelScope)

    val favoriteDogs = userRepository.userFavorites
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

    override fun onCleared() {
        appointmentStatusSubscription.close()
        dogSubscription.close()
    }
}