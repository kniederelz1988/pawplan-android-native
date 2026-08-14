package de.kniederelz.pawplan.appointments.presentation.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.appointments.presentation.booking.AppointmentBookingFragment
import de.kniederelz.pawplan.appointments.presentation.overview.adapter.AppointmentFastSelectionAdapter
import de.kniederelz.pawplan.appointments.presentation.overview.adapter.AppointmentOverviewAdapter
import de.kniederelz.pawplan.appointments.presentation.remark.AppointmentRatingFragment
import de.kniederelz.pawplan.appointments.repositories.AppointmentData
import de.kniederelz.pawplan.databinding.FragmentAppointmentOverviewBinding
import de.kniederelz.pawplan.dogs.domain.Dog
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppointmentsOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAppointmentOverviewBinding

    private val viewModel: AppointmentsOverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.appointments.observe(viewLifecycleOwner) { appointmentData ->
            if (appointmentData.isNotEmpty()) {
                binding.noUpcomingAppointments.visibility = View.GONE
                binding.upcomingAppointmentList.visibility = View.VISIBLE
            } else {
                binding.noUpcomingAppointments.visibility = View.VISIBLE
                binding.upcomingAppointmentList.visibility = View.GONE
            }

            binding.upcomingAppointmentList.adapter = AppointmentOverviewAdapter(
                appointmentData,
                { onAppointmentStartCompleteSubmit(it) },
                { onAppointmentCancelButtonSubmit(it) }
            )
        }

        viewModel.favoriteDogs.observe(viewLifecycleOwner) { favoriteDogs ->
            binding.fastSelectRecycler.adapter = AppointmentFastSelectionAdapter(
                favoriteDogs.toList()
            ) { onFastSelectButtonSubmit(it) }
        }

        parentFragmentManager.setFragmentResultListener(
            AppointmentRatingFragment.REQUEST_KEY,
            viewLifecycleOwner
        ) { _, result ->
            lifecycleScope.launch {
                val appointmentId =
                    result.getString(AppointmentRatingFragment.APPOINTMENT_KEY)
                        ?: return@launch

                val submitRating = result.getBoolean(AppointmentRatingFragment.SUBMIT_KEY)
                if (submitRating) {
                    val rating =
                        result.getInt(AppointmentRatingFragment.RATING_KEY)
                    val comment =
                        result.getString(AppointmentRatingFragment.COMMENT_KEY)
                            ?: return@launch

                    viewModel.setRating(appointmentId, rating, comment)
                }

                viewModel.completeAppointment(appointmentId)
            }
        }
    }

    private fun onAppointmentStartCompleteSubmit(data: AppointmentData) {
        AppointmentRatingFragment.show(parentFragmentManager, data.appointment.id)
    }
    private fun onAppointmentCancelButtonSubmit(data: AppointmentData) {
        lifecycleScope.launch {
            viewModel.cancelAppointment(data.appointmentStatus.appointmentId)
        }
    }

    private fun onFastSelectButtonSubmit(dog: Dog) {
        AppointmentBookingFragment().apply {
            arguments = Bundle().apply {
                putString("dogId", dog.id)
            }
        }.show(parentFragmentManager, AppointmentBookingFragment.TAG)
    }
}