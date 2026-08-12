package de.kniederelz.pawplan.tracking.presentation.overview

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.core.extensions.toLong
import de.kniederelz.pawplan.core.ui.extensions.setBoundingBox
import de.kniederelz.pawplan.databinding.FragmentTrackerBinding
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionLauncher
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionManager
import de.kniederelz.pawplan.tracking.presentation.overview.adapter.TrackerAppointmentAdapter
import de.kniederelz.pawplan.tracking.presentation.remark.TrackerRemarkFragment
import de.kniederelz.pawplan.tracking.presentation.report.TrackerReportFragment
import de.kniederelz.pawplan.tracking.repositories.base.domain.LatLngTime
import de.kniederelz.pawplan.tracking.services.WalkingTrackerServiceController
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class TrackerOverviewFragment : Fragment() {
    @Inject
    lateinit var serviceController: WalkingTrackerServiceController

    @Inject
    lateinit var permissionManager: WalkingTrackerPermissionManager
    private val permissionLauncher = WalkingTrackerPermissionLauncher(this,
        { onServicePermissionsGranted() }
    )

    private val viewModel: TrackerOverviewViewModel by viewModels()

    private lateinit var binding: FragmentTrackerBinding
    private lateinit var locationMarker: Marker
    private lateinit var routeOverlay: Polyline
    private lateinit var reportOverlay: ItemizedIconOverlay<OverlayItem>


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

        locationMarker = Marker(binding.wtMap).apply {
            icon = ContextCompat.getDrawable(requireContext(),R.drawable.navigation_tracker)
        }
        binding.wtMap.overlays.add(locationMarker)

        routeOverlay = Polyline(binding.wtMap)
        binding.wtMap.overlays.add(routeOverlay)
        binding.wtMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.wtMap.setMultiTouchControls(true)

        reportOverlay = ItemizedIconOverlay(
            mutableListOf(),
            ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_action_warning
            ),
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    // User tapped this GPS point
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    return false
                }
            },
            requireContext()
        )
        binding.wtMap.overlays.add(reportOverlay)

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()

        viewModel.clearInspectedSession()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.startButton.setOnClickListener {
            displayStartDialog()
        }
        binding.stopButton.setOnClickListener {
            displayStopDialog()
        }
        binding.reportButton.setOnClickListener {
            displayReportDialog()
        }

        viewModel.location.observe(viewLifecycleOwner) { location ->
            if (location == null)
                return@observe

            val point = GeoPoint(location.latitude, location.longitude)
            locationMarker.position = point

            binding.wtMap.controller.animateTo(point)
            binding.wtMap.controller.setZoom(20.0)
            binding.wtMap.invalidate()
        }

        viewModel.nextAppointment.observe(viewLifecycleOwner) { appointmentData ->
            binding.noUpcomingAppointments.visibility = View.VISIBLE
            binding.upcomingAppointments.visibility = View.GONE

            appointmentData?.let {
                binding.noUpcomingAppointments.visibility = View.GONE
                binding.upcomingAppointments.visibility = View.VISIBLE
                binding.upcomingAppointments.adapter =
                    TrackerAppointmentAdapter(listOf(it), {})
            }
        }
        viewModel.completedAppointments.observe(viewLifecycleOwner) { appointmentData ->
            binding.noCompletedAppointments.visibility = View.VISIBLE
            binding.completedAppointments.visibility = View.GONE

            if (appointmentData.count() > 0) {
                binding.noCompletedAppointments.visibility = View.GONE
                binding.completedAppointments.visibility = View.VISIBLE
                binding.completedAppointments.adapter =
                    TrackerAppointmentAdapter(
                        appointmentData.take(3),
                        {
                            val session = it.appointmentSession
                                ?: return@TrackerAppointmentAdapter

                            if (viewModel.canInspectSession.value == false) {
                                displayInspectDialog()
                                return@TrackerAppointmentAdapter
                            }

                            viewModel.toggleInspectedSession(session)
                        }
                    )
            }
        }

        viewModel.startTrackingEnabled.observe(viewLifecycleOwner) {
            binding.startButton.isEnabled = it
        }
        viewModel.stopTrackingEnabled.observe(viewLifecycleOwner) {
            binding.stopButton.isEnabled = it
        }
        viewModel.reportEnabled.observe(viewLifecycleOwner) {
            binding.reportButton.isVisible = it
        }

        viewModel.trackingSession.observe(viewLifecycleOwner) { session ->
            (binding.completedAppointments.adapter as TrackerAppointmentAdapter?)?.let {
                it.setHighlightedSession(session)
            }

            (binding.upcomingAppointments.adapter as TrackerAppointmentAdapter?)?.let {
                it.setHighlightedSession(session)
            }
        }

        viewModel.reports.observe(viewLifecycleOwner) { reports ->
            reportOverlay.removeAllItems()
            reportOverlay.addItems(reports.map { report ->
                OverlayItem(
                    "Incident",
                    report.description,
                    GeoPoint(
                        report.location.latitude,
                        report.location.longitude
                    )
                )
            })
            binding.wtMap.invalidate()
        }

        viewModel.routePath.observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                routeOverlay.setPoints(emptyList<GeoPoint>())
                binding.wtMap.invalidate()
                return@observe
            }

            routeOverlay.setPoints(it)
            binding.wtMap.invalidate()
        }
        viewModel.routeBoundingBox.observe(viewLifecycleOwner) {
            if (it == null) {
                return@observe
            }

            binding.wtMap.setBoundingBox(it)
            binding.wtMap.invalidate()
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

    private fun displayStartDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.tracker_startWalk_title))
            .setMessage(getString(R.string.tracker_startWalk_message))
            .setNegativeButton(getString(R.string.tracker_startWalk_negativeLabel), null)
            .setPositiveButton(getString(R.string.tracker_startWalk_positiveLabel)) { _, _ ->
                onServicePermissionsGranted()
            }
            .show()
    }

    private fun onServicePermissionsGranted() {
        if (permissionLauncher.requestNextPermission(permissionManager)) {
            val appointmentId = viewModel.nextAppointment.value?.id
                ?: return

            serviceController.startTracking(appointmentId)
        }
    }

    private fun displayStopDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.tracker_stopWalk_title))
            .setMessage(getString(R.string.tracker_stopWalk_message))
            .setNegativeButton(getString(R.string.tracker_stopWalk_negativeLabel), null)
            .setPositiveButton(getString(R.string.tracker_stopWalk_positiveLabel)) { _, _ ->
                serviceController.stopTracking()
                onCompleteAppointment()
            }
            .show()
    }
    private fun displayInspectDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.tracker_inspectWalk_title))
            .setMessage(getString(R.string.tracker_inspectWalk_message))
            .setNeutralButton(R.string.tracker_inspectWalk_label) { _, _ -> }
            .show()
    }
    private fun displayReportDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.tracker_reportIncident_title))
            .setMessage(getString(R.string.tracker_reportIncident_message))
            .setNegativeButton(getString(R.string.tracker_reportIncident_negativeLabel)) { _, _ ->

            }
            .setPositiveButton(getString(R.string.tracker_reportIncident_positiveLabel)) { _, _ ->
                onReportIncident()
            }
            .show()
    }

    private fun onCompleteAppointment() {
        val appointmentData = viewModel.nextAppointment.value
            ?: return

        lifecycleScope.launch {
            viewModel.completeAppointment(appointmentData.appointmentStatus)

            TrackerRemarkFragment.show(parentFragmentManager, appointmentData.id)
        }
    }
    private fun onReportIncident() {
        val session = viewModel.trackingSession.value
            ?: return

        val location = viewModel.location.value
            ?: return

        val time = LocalDateTime.now()
        val locationTime = LatLngTime(location.latitude, location.longitude, time.toLong())

        TrackerReportFragment.show(
            parentFragmentManager,
            session.appointmentId,
            session.id,
            locationTime
        )
    }
}