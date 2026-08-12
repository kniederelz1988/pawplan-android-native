package de.kniederelz.pawplan.dogs.representation.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentDogsOverviewBinding
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.representation.overview.adapter.DogOverviewAdapter
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DogsOverviewFragment : Fragment() {
    private lateinit var binding: FragmentDogsOverviewBinding

    private val viewModel: DogsOverviewViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDogsOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.overviewDogs.observe(viewLifecycleOwner) {
            binding.dogOverviewList.adapter = DogOverviewAdapter(
                it,
                { onDogButtonSubmit(it) },
                { onDogFavoriteButtonSubmit(it) }
            )
        }
    }

    private fun onDogButtonSubmit(dog: Dog) {
        val navController = findNavController()
        navController.navigate(R.id.dogsDetailsView, Bundle().apply {
            putString("dogId", dog.id)
        })
    }
    private fun onDogFavoriteButtonSubmit(dog: Dog) {
        lifecycleScope.launch {
            viewModel.toggleDogFavorite(dog)
        }
    }
}