package de.kniederelz.pawplan.dogs.representation.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentDogsDetailsBinding
import de.kniederelz.pawplan.dogs.domain.getAge
import de.kniederelz.pawplan.appointments.presentation.BookAppointmentFragment
import de.kniederelz.pawplan.ui.extensions.setAge
import de.kniederelz.pawplan.ui.extensions.setFavorite
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DogsDetailsFragment : Fragment() {
    private lateinit var binding: FragmentDogsDetailsBinding

    private val viewModel: DogsDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentDogsDetailsBinding.inflate(inflater, container, false)
        binding.favButton.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.dog.firstOrNull()?.let { dog ->
                    viewModel.toggleDogFavorite(dog )
                }
            }
        }
        binding.ratingButton.setOnClickListener {
            val navController = findNavController()

            // navigate to remarks page, use arguments as dog id
            navController.navigate(R.id.dogsRemarksView, Bundle().apply {
                putString("dogId", arguments?.getString("dogId"))
            })
        }
        binding.bookAppointmentButton.setOnClickListener {
            BookAppointmentFragment().apply {
                arguments = Bundle().apply {
                    putString("dogId", arguments?.getString("dogId"))
                }
            }.show(childFragmentManager, BookAppointmentFragment.TAG)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dog.collect { dog ->
                dog ?: return@collect

                binding.dogNameLabel.text = dog.name
                binding.dogImageView.load(dog.imageURL) {
                    placeholder(R.drawable.dog_placeholder)
                    error(R.drawable.dog_placeholder)
                    crossfade(true)
                }

                binding.favButton.setFavorite(dog.isFavorite)

                binding.ageCardValue.setAge(dog.getAge())
                binding.genderCardValue.text = dog.gender.toString()
                binding.sizeCardValue.text = dog.size.toString()

                binding.descriptionValue.text = dog.description
            }
        }
    }
}