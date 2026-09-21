package de.kniederelz.pawplan.appointments.notifications

import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.scopes.ApplicationScope
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentNotificationSync @Inject constructor(
    @param:ApplicationScope private val scope: CoroutineScope,
    private val userRepository: UserRepository,
    private val appointmentsRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val notificationManager: AppointmentNotificationManager,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    private val appointmentFlow = userRepository.userProfile
        .flatMapLatest { userProfile ->
            if (userProfile == null) {
                return@flatMapLatest flowOf(emptyList())
            }

            appointmentsRepository.observeUpcomingVolunteerAppointments(userProfile.id)
        }
        .flatMapLatest { appointments ->

            val appointmentMap =
                appointments.associateBy { it.id }

            appointmentStatusRepository.observeStatus(appointments.map { it.id })
                .map { statuses ->
                    statuses.values.filter { it.status == AppointmentStatusType.CONFIRMED }
                        .mapNotNull {

                            val appointment = appointmentMap[it.appointmentId]
                                ?: return@mapNotNull null

                            AppointmentNotificationData(
                                id = it.appointmentId,
                                time = appointment.date
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant(),

                                status = it.status
                            )

                        }
                }
        }

    private var previousAppointments: AppointmentNotificationMap = emptyMap()
    private var syncJob: Job? = null

    fun start() {
        if (syncJob?.isActive == true)
            return

        notificationManager.createNotificationChannel()

        syncJob = scope.launch {
            appointmentFlow.collect { appointments ->
                val currentAppointments = appointments.associateBy { it.id }

                val plan = planAppointmentNotificationSync(
                    previous = previousAppointments,
                    current = currentAppointments
                )

                plan.notificationsToCancel.forEach(notificationManager::cancel)
                plan.notificationsToSchedule.forEach(notificationManager::schedule)

                previousAppointments = currentAppointments
            }
        }
    }
}