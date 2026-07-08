package de.kniederelz.pawplan.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import de.kniederelz.pawplan.viewModels.AppointmentsOverviewViewModel
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.ActivityMainBinding
import de.kniederelz.pawplan.databinding.FragmentAppointmentsOverviewBinding
import de.kniederelz.pawplan.fragments.BookAppointmentFragment

class AppointmentsOverviewView : Fragment() {

    companion object {
        fun newInstance() = AppointmentsOverviewView()
    }

    private lateinit var binding: FragmentAppointmentsOverviewBinding

    private val viewModel: AppointmentsOverviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentsOverviewBinding.inflate(inflater, container, false)
        binding.button.setOnClickListener {
            BookAppointmentFragment.newInstance("Waldi").show(parentFragmentManager, BookAppointmentFragment.TAG)
        }

        return binding.root
    }
}