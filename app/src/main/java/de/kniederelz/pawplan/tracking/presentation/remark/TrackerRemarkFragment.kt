package de.kniederelz.pawplan.tracking.presentation.remark

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating
import de.kniederelz.pawplan.databinding.FragmentTrackerRemarkBinding
import de.kniederelz.pawplan.tracking.presentation.overview.TrackerOverviewViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.getValue

@AndroidEntryPoint
class TrackerRemarkFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "RemarkBottomSheetDialogFragment"
    }

    private lateinit var binding: FragmentTrackerRemarkBinding

    private val viewModel: TrackerRemarkViewModel by viewModels()

    private lateinit var stars: List<ImageButton>
    private var rating = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTrackerRemarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stars = listOf(binding.star1, binding.star2, binding.star3, binding.star4, binding.star5)
        stars.forEachIndexed { index, button ->
            button.setOnClickListener {
                setRating(index + 1)
            }
        }

        binding.submitButton.setOnClickListener { _ ->
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.appointments.first().map { it.value }.firstOrNull()?.let { appointment ->
                    val rating = AppointmentRating(
                        id = "",
                        appointmentId = appointment.id,
                        volunteerId = appointment.volunteerId,
                        dogId = appointment.dogId,
                        rating = rating,
                        comment = binding.remarkDescription.text.toString(),
                        updatedAt = LocalDateTime.now()
                    )
                    viewModel.createRating(rating)
                }

                dismiss()
            }
        }
    }

    private fun setRating(value: Int) {
        rating = value

        val selectedColor = ContextCompat.getColor(requireContext(), R.color.md_theme_outline)
        val unselectedColor = ContextCompat.getColor(requireContext(), R.color.md_theme_outlineVariant)

        stars.forEachIndexed { index, button ->
            button.imageTintList = ColorStateList.valueOf(
                if (index < value)
                    selectedColor
                else
                    unselectedColor
            )

            button.animate()
                .scaleX(if (index < value) 1.15f else 1f)
                .scaleY(if (index < value) 1.15f else 1f)
                .setDuration(150)
                .start()
        }
    }
}