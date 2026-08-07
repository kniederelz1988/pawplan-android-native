package de.kniederelz.pawplan.tracking.presentation.remark

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingRepository
import javax.inject.Inject

@HiltViewModel
class TrackerRemarkViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val appointmentRepository: AppointmentRepository,
    private val appointmentRemarkRepository: AppointmentRatingRepository,
) : ViewModel() {

    private val appointmentId: String =
        checkNotNull(savedStateHandle["appointmentId"])

    private val appointmentSubscription = appointmentRepository.createSubscription()
        .registerListener(appointmentId)
    val appointments = appointmentSubscription.values

    suspend fun createRating(rating: AppointmentRating) {
        appointmentRemarkRepository.createRating(rating)
            .onSuccess { Log.d("TrackerRemarkViewModel", "Rating created") }
            .onFailure { Log.e("TrackerRemarkViewModel", "Rating creation failed", it) }
    }
}