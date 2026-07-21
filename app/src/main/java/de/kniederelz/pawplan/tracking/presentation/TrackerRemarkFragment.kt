package de.kniederelz.pawplan.tracking.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentTrackerRemarkBinding

/**
 * A simple [Fragment] subclass.
 * Use the [TrackerRemarkFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TrackerRemarkFragment : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "RemarkBottomSheetDialogFragment"

        @JvmStatic
        fun newInstance() = TrackerRemarkFragment()
    }

    private lateinit var binding: FragmentTrackerRemarkBinding

    private lateinit var stars: List<ImageButton>
    private var rating = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackerRemarkBinding.inflate(inflater, container, false)

        stars = listOf(
            binding.star1,
            binding.star2,
            binding.star3,
            binding.star4,
            binding.star5
        )

        stars.forEachIndexed { index, button ->
            button.setOnClickListener {
                setRating(index + 1)
            }
        }
        // Inflate the layout for this fragment
        return binding.root
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