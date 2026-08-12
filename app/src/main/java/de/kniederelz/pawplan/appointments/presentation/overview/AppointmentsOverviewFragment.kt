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
            binding.appointmentList.adapter = AppointmentOverviewAdapter(
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
    }

    private fun onAppointmentStartCompleteSubmit(data: AppointmentData) {
        lifecycleScope.launch {
            viewModel.completeAppointment(data.appointmentStatus)

            AppointmentRatingFragment.show(parentFragmentManager, data.appointment.id)
        }
    }
    private fun onAppointmentCancelButtonSubmit(data: AppointmentData) {
        lifecycleScope.launch {
            viewModel.cancelAppointment(data.appointmentStatus)
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