package de.kniederelz.pawplan.appointments.notifications

import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusRepository
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.scopes.ApplicationScope
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppointmentNotificationSync @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val userRepository: UserRepository,
    private val appointmentsRepository: AppointmentRepository,
    private val appointmentStatusRepository: AppointmentStatusRepository,
    private val notificationManager: AppointmentNotificationManager,
) {
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
                .mapLatest { statuses ->
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

    fun start() {
        notificationManager.createNotificationChannel()

        scope.launch {
            appointmentFlow.collect {
                previousAppointments = sync(
                    previous = previousAppointments,
                    current = it.associateBy { appointment -> appointment.id }
                )
            }
        }
    }

    private fun sync(
        previous: AppointmentNotificationMap,
        current: AppointmentNotificationMap
    ) : AppointmentNotificationMap {

        // New or changed appointments
        current.forEach { (id, appointment) ->
            val oldAppointment = previous[id]
            if (oldAppointment != appointment) {
                oldAppointment?.let { notificationManager.cancel(it) }
                notificationManager.schedule(appointment)
            }
        }

        // Deleted appointments
        previous
            .minus(current.keys)
            .forEach { entry ->
                notificationManager.cancel(entry.value)
            }

        return current
    }
}