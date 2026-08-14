package de.kniederelz.pawplan.tracking.presentation.report

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.core.extensions.toLocalTime
import de.kniederelz.pawplan.tracking.repositories.base.domain.LatLngTime
import de.kniederelz.pawplan.tracking.repositories.reports.domain.IncidentReport
import de.kniederelz.pawplan.tracking.repositories.reports.domain.IncidentReportRepository
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TrackerReportViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val userRepository: UserRepository,
    private val incidentReportRepository: IncidentReportRepository,
) : ViewModel() {
    private val _sessionId: String =
        checkNotNull(savedStateHandle["sessionId"])

    private val _locationJson: String =
        checkNotNull(savedStateHandle["locationJson"])

    private val _location = MutableLiveData(LatLngTime())
    val location: LiveData<LatLngTime> = _location

    private val _date = MutableLiveData(LocalDate.now())
    val date: LiveData<LocalDate> = _date

    private val _time = MutableLiveData(LocalTime.now())
    val time: LiveData<LocalTime> = _time

    init {
        val t = Json.decodeFromString<LatLngTime>(_locationJson)
        _location.value = t
        _date.value = t.timestamp.toLocalDate()
        _time.value = t.timestamp.toLocalTime()
    }

    private val userProfile = userRepository.userProfile.asLiveData()
    suspend fun createIncident(description: String) {
        val userProfile = userProfile.value
            ?: return

        val location = _location.value
            ?: return

        val rating = IncidentReport(
            id = "",
            sessionId = _sessionId,
            location = location,
            description = description,
            reportedBy = userProfile.id,
            reportedAt = LocalDateTime.now()
        )
        incidentReportRepository.createReport(rating)
            .onSuccess { Log.d("TrackerRemarkViewModel", "Report created") }
            .onFailure { Log.e("TrackerRemarkViewModel", "Report creation failed", it) }
    }
}