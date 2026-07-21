package de.kniederelz.pawplan.dogs.representation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentDogsBinding

/**
 * A simple [androidx.fragment.app.Fragment] subclass.
 * Use the [DogsView.newInstance] factory method to
 * create an instance of this fragment.
 */
class DogsView : Fragment() {

    companion object {
        fun newInstance() = DogsView()
    }

    private lateinit var binding: FragmentDogsBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDogsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navHostFragment = childFragmentManager.findFragmentById(R.id.dogs_navHost) as NavHostFragment
        navController = navHostFragment.navController

        binding.backButton.setOnClickListener {
            navController.popBackStack()
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.backButton.isVisible =
                (destination.id != R.id.dogsOverviewView)
        }
    }
}