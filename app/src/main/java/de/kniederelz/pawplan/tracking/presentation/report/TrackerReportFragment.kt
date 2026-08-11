package de.kniederelz.pawplan.tracking.presentation.report

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.timeFormatter
import de.kniederelz.pawplan.databinding.FragmentTrackerReportBinding
import de.kniederelz.pawplan.tracking.presentation.remark.TrackerReportViewModel
import de.kniederelz.pawplan.tracking.repositories.base.domain.LatLngTime
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker

@AndroidEntryPoint
class TrackerReportFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "ReportBottomSheetDialogFragment"

        fun show(
            fragmentManager: FragmentManager,
            appointmentId: String,
            sessionId: String,
            locationTime: LatLngTime
        ) {
            TrackerReportFragment().apply {
                arguments = Bundle().apply {
                    putString("appointmentId", appointmentId)
                    putString("sessionId", sessionId)
                    putString("locationJson", Json.encodeToString(locationTime))
                }
            }.show(fragmentManager, TAG)
        }
    }

    private val viewModel: TrackerReportViewModel by viewModels()

    private lateinit var binding: FragmentTrackerReportBinding
    private lateinit var locationMarker: Marker


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val prefs = context?.getSharedPreferences(
            "app_preferences",
            Context.MODE_PRIVATE
        )

        // OSMDroid Configuration - Must be done before inflating layout
        val omsdriodConfig = Configuration.getInstance()
        omsdriodConfig.userAgentValue = context?.packageName
        omsdriodConfig.load(context, prefs)

        // Inflate the layout for this fragment
        binding = FragmentTrackerReportBinding.inflate(inflater, container, false)

        locationMarker = Marker(binding.reportMap).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = ContextCompat.getDrawable(requireContext(),R.drawable.ic_action_warning)
        }
        binding.reportMap.overlays.add(locationMarker)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.location.observe(viewLifecycleOwner) {
            val point = GeoPoint(it.latitude, it.longitude)
            binding.reportMap.controller.setCenter(point)
            binding.reportMap.controller.setZoom(20.0)

            locationMarker.position = point
        }
        viewModel.date.observe(viewLifecycleOwner) {
            binding.selectDateButton.text = it.format(dateFormatter)
        }
        viewModel.time.observe(viewLifecycleOwner) {
            binding.selectTimeButton.text = it.format(timeFormatter)
        }

        binding.submitButton.setOnClickListener {
            val description = binding.reportDescription.text.toString()
            if (description.isEmpty()) {
                binding.reportDescription.error =
                    getString(R.string.wtp_report_emptyerror)
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.createIncident(description)
                dismiss()
            }
        }
    }
}