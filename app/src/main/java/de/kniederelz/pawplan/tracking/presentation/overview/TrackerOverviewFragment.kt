package de.kniederelz.pawplan.tracking.presentation.overview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.appointments.presentation.AppointmentBookingFragment
import de.kniederelz.pawplan.core.ui.extensions.showPosition
import de.kniederelz.pawplan.databinding.FragmentTrackerBinding
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionManager
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionState
import de.kniederelz.pawplan.tracking.presentation.overview.adapter.TrackerAppointmentAdapter
import de.kniederelz.pawplan.tracking.presentation.remark.TrackerRemarkFragment
import de.kniederelz.pawplan.tracking.presentation.report.TrackerReportFragment
import de.kniederelz.pawplan.tracking.services.WalkingTrackerService
import de.kniederelz.pawplan.tracking.services.WalkingTrackerState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import javax.inject.Inject

@AndroidEntryPoint
class TrackerOverviewFragment : Fragment() {
    @Inject
    lateinit var permissionManager: WalkingTrackerPermissionManager

    private lateinit var binding: FragmentTrackerBinding

    private val viewModel: TrackerOverviewViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val prefs = context?.getSharedPreferences(
            "app_preferences",
            Context.MODE_PRIVATE
        )

        // OSMDroid Configuration - Must be done before inflating layout
        val omsdriodConfig = Configuration.getInstance()
        omsdriodConfig.userAgentValue = context?.packageName
        omsdriodConfig.load(context, prefs)

        binding = FragmentTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            displayStartTrackingDialog()
        }
        binding.stopButton.setOnClickListener {
            displayStopTrackingDialog()
        }
        binding.reportButton.setOnClickListener {
            TrackerReportFragment.newInstance()
                .show(parentFragmentManager, TrackerReportFragment.TAG)
        }

        val locationMarker = Marker(binding.wtMap).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = ContextCompat.getDrawable(requireContext(),R.drawable.navigation_tracker)
        }
        binding.wtMap.overlays.add(locationMarker)

        val polylineOverlay = Polyline(binding.wtMap)
        binding.wtMap.overlays.add(polylineOverlay)
        binding.wtMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.wtMap.setMultiTouchControls(true)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.session.collectLatest { session ->
                Log.d("TrackerOverviewFragment", "Session: $session")

                session?.let {
                    polylineOverlay.setPoints(session.locations.map {
                        GeoPoint(
                            it.latitude,
                            it.longitude
                        )
                    })
                }

                binding.wtMap.invalidate()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.location.collectLatest { location ->
                locationMarker.position = GeoPoint(location.latitude, location.longitude)

                binding.wtMap.showPosition(location.latitude, location.longitude, 20.0)
                binding.wtMap.invalidate()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collectLatest {
                binding.startButton.isEnabled = (it == WalkingTrackerState.Stopped)
                binding.stopButton.isEnabled = (it == WalkingTrackerState.Started)

                binding.reportButton.isVisible = (it == WalkingTrackerState.Started)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.nextAppointment.collectLatest { appointmentData ->
                binding.noUpcomingAppointments.visibility = View.VISIBLE
                binding.upcomingAppointments.visibility = View.GONE

                appointmentData?.let {
                    binding.noUpcomingAppointments.visibility = View.GONE
                    binding.upcomingAppointments.visibility = View.VISIBLE
                    binding.upcomingAppointments.adapter = TrackerAppointmentAdapter(listOf(it))
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.wtMap.onDetach()
    }

    override fun onPause() {
        super.onPause()

        binding.wtMap.onPause()
    }
    override fun onResume() {
        super.onResume()

        binding.wtMap.onResume()
    }

    private fun displayStartTrackingDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.tracker_startWalk_title))
            .setMessage(getString(R.string.tracker_startWalk_message))
            .setNegativeButton(getString(R.string.tracker_startWalk_negativeLabel), null)
            .setPositiveButton(getString(R.string.tracker_startWalk_positiveLabel)) { _, _ ->
                requestNextPermission()
            }
            .show()
    }
    private fun displayStopTrackingDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.tracker_startWalk_title))
            .setMessage(getString(R.string.tracker_startWalk_message))
            .setNegativeButton(getString(R.string.tracker_startWalk_negativeLabel), null)
            .setPositiveButton(getString(R.string.tracker_startWalk_positiveLabel)) { _, _ ->
                stopTrackingService()

                lifecycleScope.launch {
                    viewModel.nextAppointment.first()?.let { trackerAppointmentData ->
                        TrackerRemarkFragment().apply {
                            arguments = Bundle().apply {
                                putString("appointmentId", trackerAppointmentData.id)
                            }
                        }.show(parentFragmentManager, TrackerRemarkFragment.TAG)
                    }
                }
            }
            .show()
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                requestNextPermission()
            }
        }

    private val foregroundPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.values.all { it }) {
                requestNextPermission()
            }
        }

    private val backgroundPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                requestNextPermission()
            }
        }

    private fun requestNextPermission() {
        val state = permissionManager.getPermissionState()
        when (state) {

            WalkingTrackerPermissionState.Ready ->
                startTrackingService()

            WalkingTrackerPermissionState.NeedsForegroundLocation ->
                foregroundPermissionLauncher.launch(
                    permissionManager.foregroundPermissions()
                )

            WalkingTrackerPermissionState.NeedsBackgroundLocation ->
                backgroundPermissionLauncher.launch(
                    permissionManager.backgroundPermission()
                )

            WalkingTrackerPermissionState.NeedsNotifications ->
                notificationPermissionLauncher.launch(
                    permissionManager.notificationPermission()
                )
        }
    }

    private fun startTrackingService() {
        lifecycleScope.launch {
            viewModel.nextAppointment.first { it != null }?.let { trackerAppointmentData ->
                val intent = Intent(requireContext(), WalkingTrackerService::class.java)
                intent.action = "START_TRACKING"
                intent.putExtra("appointmentId", trackerAppointmentData.id)

                ContextCompat.startForegroundService(requireContext(), intent)
            }
        }
    }
    private fun stopTrackingService() {
        val intent = Intent(requireContext(), WalkingTrackerService::class.java)
        intent.action = "STOP_TRACKING"

        ContextCompat.startForegroundService(requireContext(), intent)
    }
}