package de.kniederelz.pawplan.dogs.representation.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentDogsOverviewBinding
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogSizeType
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

        binding.nameFilterInput.doOnTextChanged { text, _, _, _ ->
            viewModel.filterDogsByName(text.toString())
        }

        binding.filterGroup.addOnButtonCheckedListener { _, i, value ->
            when (i) {
                R.id.filterFavButton    ->
                    viewModel.filterDogsByFavs(value)
                R.id.filterSmallButton  ->
                    viewModel.filterDogsBySize(
                        DogSizeType.SMALL,
                        value
                    )
                R.id.filterMediumButton ->
                    viewModel.filterDogsBySize(
                        DogSizeType.MEDIUM,
                        value
                    )
                R.id.filterLargeButton  ->
                    viewModel.filterDogsBySize(
                        DogSizeType.LARGE,
                        value
                    )
            }
        }

        viewModel.overviewDogFilter.observe(viewLifecycleOwner) {
            binding.filterGroup.clearChecked()

            if (it.filterByFavorites)
                binding.filterGroup.check(R.id.filterFavButton)

            if (it.filterBySize.contains(DogSizeType.SMALL))
                binding.filterGroup.check(R.id.filterSmallButton)

            if (it.filterBySize.contains(DogSizeType.MEDIUM))
                binding.filterGroup.check(R.id.filterMediumButton)

            if (it.filterBySize.contains(DogSizeType.LARGE))
                binding.filterGroup.check(R.id.filterLargeButton)
        }
        viewModel.overviewDogs.observe(viewLifecycleOwner) { dogOverviewData ->
            binding.noResultsLayout.visibility = if(dogOverviewData.isEmpty())
                    View.VISIBLE
                else
                    View.GONE

            binding.dogOverviewList.visibility = if(dogOverviewData.isEmpty())
                    View.GONE
                else
                    View.VISIBLE

            binding.dogOverviewList.adapter = DogOverviewAdapter(
                dogOverviewData,
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