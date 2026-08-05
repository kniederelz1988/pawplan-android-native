package de.kniederelz.pawplan.appointments.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.appointments.presentation.adapter.AppointmentFastSelectionAdapter
import de.kniederelz.pawplan.appointments.presentation.adapter.AppointmentOverviewAdapter
import de.kniederelz.pawplan.databinding.FragmentAppointmentOverviewBinding
import de.kniederelz.pawplan.dogs.domain.Dog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AppointmentsOverviewFragment : Fragment() {
    private lateinit var binding: FragmentAppointmentOverviewBinding

    private var overviewAdapter: AppointmentOverviewAdapter = AppointmentOverviewAdapter()
    private var fastSelectionAdapter: AppointmentFastSelectionAdapter =
        AppointmentFastSelectionAdapter(
            { onFastSelectItemSubmit(it) }
        )
    private val viewModel: AppointmentsOverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppointmentOverviewBinding.inflate(inflater, container, false)
        binding.appointmentList.adapter = overviewAdapter
        binding.fastSelectRecycler.adapter = fastSelectionAdapter

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.appointments.collectLatest { pagingData ->
                    Log.d("AppointmentsOverviewFragment", "Paging data: $pagingData")
                    overviewAdapter.submitData(pagingData)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.favoriteDogs.collectLatest { pagingData ->
                    Log.d("AppointmentsOverviewFragment", "Paging data: $pagingData")
                    fastSelectionAdapter.submitData(pagingData)
                }
            }
        }
    }

    private fun onFastSelectItemSubmit(dog: Dog) {
        AppointmentBookingFragment().apply {
            arguments = Bundle().apply {
                putString("dogId", dog.id)
            }
        }.show(parentFragmentManager, AppointmentBookingFragment.TAG)
    }
}