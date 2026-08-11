package de.kniederelz.pawplan.dogs.representation.details

import android.os.Bundle
import android.util.Log
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
import de.kniederelz.pawplan.appointments.presentation.booking.AppointmentBookingFragment
import de.kniederelz.pawplan.databinding.FragmentDogsDetailsBinding
import de.kniederelz.pawplan.dogs.domain.extensions.getAge
import de.kniederelz.pawplan.ui.extensions.setAge
import de.kniederelz.pawplan.ui.extensions.setFavorite
import de.kniederelz.pawplan.core.ui.extensions.setGender
import de.kniederelz.pawplan.core.ui.extensions.setRating
import de.kniederelz.pawplan.core.ui.extensions.setSize
import de.kniederelz.pawplan.ui.extensions.setStatisticsAverage
import de.kniederelz.pawplan.ui.extensions.setStatisticsCount
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
            val dogId = arguments?.getString("dogId")
                ?: return@setOnClickListener

            // navigate to remarks page, use arguments as dog id
            val navController = findNavController()
            navController.navigate(R.id.dogsRemarksView, Bundle().apply {
                putString("dogId", dogId)
            })
        }
        binding.bookAppointmentButton.setOnClickListener {
            val dogId = arguments?.getString("dogId")
                ?: return@setOnClickListener

            Log.d("DogsDetailsFragment", "Creating appointment for dog $dogId")
            AppointmentBookingFragment().apply {
                arguments = Bundle().apply {
                    putString("dogId", dogId)
                }
            }.show(childFragmentManager, AppointmentBookingFragment.TAG)
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

                binding.ageCardValue.setAge(dog.getAge())
                binding.genderCardImage.setGender(dog.gender)
                binding.sizeCardImage.setSize(dog.size)

                binding.descriptionValue.text = dog.description

                binding.ratingText.setStatisticsAverage(dog.statistics)
                binding.ratingText2.setStatisticsCount(dog.statistics)

                binding.ratingStar1Image.setRating((dog.statistics?.average ?: 0f) >= 1f)
                binding.ratingStar2Image.setRating((dog.statistics?.average ?: 0f) >= 2f)
                binding.ratingStar3Image.setRating((dog.statistics?.average ?: 0f) >= 3f)
                binding.ratingStar4Image.setRating((dog.statistics?.average ?: 0f) >= 4f)
                binding.ratingStar5Image.setRating((dog.statistics?.average ?: 0f) >= 5f)

                binding.favButton.setFavorite(dog.isFavorite)
            }
        }
    }
}