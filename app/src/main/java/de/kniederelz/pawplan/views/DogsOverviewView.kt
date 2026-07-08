package de.kniederelz.pawplan.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import de.kniederelz.pawplan.R

import de.kniederelz.pawplan.viewModels.DogsOverviewViewModel
import de.kniederelz.pawplan.databinding.FragmentDogsOverviewBinding

class DogsOverviewView : Fragment() {

    companion object {
        fun newInstance() = DogsOverviewView()
    }

    private lateinit var binding: FragmentDogsOverviewBinding

    private val viewModel: DogsOverviewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDogsOverviewBinding.inflate(inflater, container, false)
        binding.dogButton.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.dogsDetailsView)
        }
        return binding.root
    }
}