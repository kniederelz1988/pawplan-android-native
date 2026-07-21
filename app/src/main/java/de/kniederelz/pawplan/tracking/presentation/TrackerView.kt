package de.kniederelz.pawplan.tracking.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentTrackerBinding
import de.kniederelz.pawplan.tracking.TrackerViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory

class TrackerView : Fragment() {
    companion object {
        fun newInstance() = TrackerView()
    }

    private lateinit var binding: FragmentTrackerBinding

    private val viewModel: TrackerViewModel by viewModels()

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

        binding = FragmentTrackerBinding.inflate(inflater, container, false)
        binding.startButton.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.tracker_startWalk_title))
                .setMessage(getString(R.string.tracker_startWalk_message))
                .setNegativeButton(getString(R.string.tracker_startWalk_negativeLabel), null)
                .setPositiveButton(getString(R.string.tracker_startWalk_positiveLabel)) { _, _ ->
                    binding.reportButton.isVisible = true

                    binding.startButton.isEnabled = false
                    binding.stopButton.isEnabled = true
                }
                .show()
        }
        binding.stopButton.setOnClickListener {

            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.tracker_startWalk_title))
                .setMessage(getString(R.string.tracker_startWalk_message))
                .setNegativeButton(getString(R.string.tracker_startWalk_negativeLabel), null)
                .setPositiveButton(getString(R.string.tracker_startWalk_positiveLabel)) { _, _ ->
                    binding.reportButton.isVisible = false

                    binding.startButton.isEnabled = true
                    binding.stopButton.isEnabled = false

                    TrackerRemarkFragment.newInstance()
                        .show(parentFragmentManager, TrackerRemarkFragment.TAG)
                }
                .show()
        }
        binding.reportButton.setOnClickListener {
            TrackerReportFragment.newInstance()
                .show(parentFragmentManager, TrackerReportFragment.TAG)
        }

        binding.wtMap.setTileSource(TileSourceFactory.MAPNIK)
        binding.wtMap.setMultiTouchControls(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        childFragmentManager.beginTransaction()
            .replace(R.id.childFragmentContainer, TrackerPendingWalkFragment())
            .commit()
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
}