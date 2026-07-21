package de.kniederelz.pawplan.appointments.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import de.kniederelz.pawplan.databinding.FragmentAppointmentsOverviewBinding

class AppointmentsOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAppointmentsOverviewBinding

    private val viewModel: AppointmentsOverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentsOverviewBinding.inflate(inflater, container, false)
        binding.button.setOnClickListener {
            BookAppointmentFragment().apply {
                arguments = Bundle().apply {
                    putString("dogId", getString("dogId"))
                }
            }
                .show(parentFragmentManager, BookAppointmentFragment.TAG)
        }

        return binding.root
    }
}