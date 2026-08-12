package de.kniederelz.pawplan.appointments.presentation.remark

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentTrackerRemarkBinding
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class AppointmentRatingFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "RemarkBottomSheetDialogFragment"

        fun show(fragmentManager: FragmentManager, appointmentId: String) {
            AppointmentRatingFragment().apply {
                arguments = Bundle().apply {
                    putString("appointmentId", appointmentId)
                }
            }.show(fragmentManager, TAG)
        }
    }

    private val viewModel: AppointmentRatingViewModel by viewModels()

    private lateinit var binding: FragmentTrackerRemarkBinding
    private lateinit var stars: List<ImageButton>
    private var rating = 5

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
        setRating(rating)

        viewModel.nextAppointment.observe(viewLifecycleOwner) {
            it?.let { data ->
                binding.remarkCaption.text = getString(R.string.wtp_remark_caption, data.dog.name)
            }
        }

        binding.submitButton.setOnClickListener { _ ->
            val appointment = viewModel.getAppointment()
                ?: return@setOnClickListener

            val comment = binding.remarkDescription.text.toString()
            if (comment.isBlank()) {
                binding.remarkDescription.error = getString(R.string.wtp_remark_emptyerror)
                return@setOnClickListener
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.submitRating(appointment,rating, comment)

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