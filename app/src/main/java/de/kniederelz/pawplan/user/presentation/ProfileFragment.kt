package de.kniederelz.pawplan.user.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.auth.AuthViewModel
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.initials
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.databinding.FragmentProfileBinding
import de.kniederelz.pawplan.core.ui.extensions.applyRole
import kotlinx.coroutines.launch
import java.time.LocalDate


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var binding: FragmentProfileBinding

    private lateinit var birthday: LocalDate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.roleBadge.background.mutate()
        binding.visitCountLabel.text = getString(
            R.string.profile_visit_value, 0
        )
        binding.distanceCountLabel.text = getString(
            R.string.profile_distance_value, 0.0
        )
        binding.favCountLabel.text = getString(
            R.string.profile_fav_value, 0
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.authState.observe(viewLifecycleOwner) { authState ->
            if (authState.isAuthenticated) {
                val email = authState.user?.email
                if (email != null) {
                    binding.emailText.text = email
                    binding.emailInput.setText(email)
                } else {
                    binding.emailText.setText(R.string.profile_email_text)
                    binding.emailInput.setText(R.string.profile_email_text)
                }
            }
        }

        profileViewModel.userProfile.observe(viewLifecycleOwner) {
            it?.let { profile ->
                birthday = profile.birthday

                binding.initialsText.text = profile.name.initials()

                binding.nameText.text = profile.name
                binding.nameInput.setText(profile.name)

                binding.phoneInput.setText(profile.phoneNumber)

                binding.initialsImage.load(profile.imageUrl) {
                    placeholder(R.drawable.dog_placeholder)
                    error(R.drawable.dog_placeholder)
                    crossfade(true)
                }

                binding.birthdayInput.setText(profile.birthday.format(dateFormatter))
                binding.volunteerSinceInput.setText(profile.volunteerSince.format(dateFormatter))
            }
        }

        profileViewModel.userRole.observe(viewLifecycleOwner) { role ->
            binding.roleBadge.applyRole(requireContext(), role)
        }

        profileViewModel.userStatistics.observe(viewLifecycleOwner) { stats ->
            binding.visitCountLabel.text = getString(
                R.string.profile_visit_value,
                stats.appointmentCount
            )
            binding.distanceCountLabel.text = getString(
                R.string.profile_distance_value,
                stats.trackedDistance
            )
            binding.favCountLabel.text = getString(
                R.string.profile_fav_value,
                stats.favoriteCount
            )
        }

        binding.birthdayInput.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.addOnPositiveButtonClickListener { selection ->
                birthday = selection.toLocalDate()
                binding.birthdayInput.setText(birthday.format(dateFormatter))
            }

            picker.show(parentFragmentManager, "date_picker")
        }

        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                authViewModel.logout()
            }
        }
        binding.editButton.setOnClickListener {
            val curProfile = profileViewModel.userProfile.value
                ?: return@setOnClickListener

            val userProfile = curProfile.copy(
                name = binding.nameInput.text.toString(),
                phoneNumber = binding.phoneInput.text.toString(),

                birthday = birthday
            )

            profileViewModel.updateProfile(userProfile)
            profileViewModel.updateUserName(userProfile.name)
        }
    }
}