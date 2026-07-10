package de.kniederelz.pawplan.views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentDogsDetailsBinding
import de.kniederelz.pawplan.fragments.BookAppointmentFragment

/**
 * A simple [Fragment] subclass.
 * Use the [DogsDetailsView.newInstance] factory method to
 * create an instance of this fragment.
 */
class DogsDetailsView : Fragment() {

    companion object {
        fun newInstance() = DogsDetailsView()
    }

    private lateinit var binding: FragmentDogsDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDogsDetailsBinding.inflate(inflater, container, false)
        binding.ratingButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.dogsRemarksView)
        }
        binding.bookAppointmentButton.setOnClickListener {
            BookAppointmentFragment.newInstance("Waldi")
                .show(childFragmentManager, BookAppointmentFragment.TAG)
        }

        return binding.root
    }
}