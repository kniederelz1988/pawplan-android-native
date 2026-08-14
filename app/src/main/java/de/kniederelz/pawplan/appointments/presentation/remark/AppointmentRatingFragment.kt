package de.kniederelz.pawplan.appointments.presentation.remark

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentTrackerRemarkBinding
import kotlin.getValue

@AndroidEntryPoint
class AppointmentRatingFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "RemarkBottomSheetDialogFragment"

        const val REQUEST_KEY = "APPOINTMENT_RATING"

        const val APPOINTMENT_KEY = "APPOINTMENT_ID"
        const val SUBMIT_KEY = "COMPLETED"
        const val RATING_KEY = "rating"
        const val COMMENT_KEY = "comment"

        fun show(fragmentManager: FragmentManager, appointmentId: String) {
            Log.d("AppointmentRatingFragment", appointmentId)

            AppointmentRatingFragment().apply {
                arguments = Bundle().apply {
                    putString(APPOINTMENT_KEY, appointmentId)
                }
            }.show(fragmentManager, TAG)
        }
    }

    private val viewModel: AppointmentRatingViewModel by viewModels()

    private lateinit var binding: FragmentTrackerRemarkBinding
    private lateinit var stars: List<ImageButton>
    private var rating = 5

    private var submitRating: Boolean = false

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
            val comment = binding.remarkDescription.text.toString()
            if (comment.isBlank()) {
                binding.remarkDescription.error = getString(R.string.wtp_remark_emptyerror)
                return@setOnClickListener
            }

            submitRating = true
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle().apply {
                    putBoolean(SUBMIT_KEY, true)
                    putString(APPOINTMENT_KEY, viewModel.appointmentId)
                    putInt(RATING_KEY, rating)
                    putString(COMMENT_KEY, comment)
                }
            )
            dismiss()
        }
        binding.closeButton.setOnClickListener { _ ->
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        if (!submitRating) {
            parentFragmentManager.setFragmentResult(
                REQUEST_KEY,
                Bundle().apply {
                    putBoolean(SUBMIT_KEY, false)
                    putString(APPOINTMENT_KEY, viewModel.appointmentId)
                }
            )
        }

        super.onDismiss(dialog)
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