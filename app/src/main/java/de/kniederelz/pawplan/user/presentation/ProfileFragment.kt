package de.kniederelz.pawplan.user.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.auth.AuthViewModel
import de.kniederelz.pawplan.core.extensions.applyColors
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.initials
import de.kniederelz.pawplan.core.extensions.toLocalDate
import de.kniederelz.pawplan.core.extensions.toRoleString
import de.kniederelz.pawplan.databinding.FragmentProfileBinding
import de.kniederelz.pawplan.user.domain.UserProfile
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var binding: FragmentProfileBinding
    private var userProfile: UserProfile = UserProfile()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        binding.roleBadge.background.mutate()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel.authState.observe(viewLifecycleOwner) { authState ->
            if (authState.isAuthenticated) {
                val user = authState.user!!

                val email = user.email
                if (email != null) {
                    binding.emailText.text = email
                    binding.emailInput.setText(email)
                } else {
                    binding.emailText.setText(R.string.profile_email_text)
                    binding.emailInput.setText(R.string.profile_email_text)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            // this doesnt get update after initial collect
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userProfile.collect { tProfile ->
                    Log.d("ProfileFragment", "Observed profile: ${tProfile?.name}")
                    if (tProfile != null) {
                        userProfile = tProfile

                        binding.initialsText.text = userProfile.name.initials()

                        binding.nameText.text = userProfile.name
                        binding.nameInput.setText(userProfile.name)

                        binding.phoneInput.setText(userProfile.phoneNumber)
                        binding.birthdayInput.setText(userProfile.birthday.format(dateFormatter))
                        binding.volunteerSinceInput.setText(userProfile.volunteerSince.format(dateFormatter))
                    } else {
                        userProfile = UserProfile()

                        binding.phoneInput.setText(R.string.profile_phone_value)
                        binding.birthdayInput.setText(R.string.profile_birthday_value)
                        binding.volunteerSinceInput.setText(R.string.profile_volunteer_value)
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userRole.collect { role ->
                    binding.roleBadge.text = role.toRoleString()
                    role.applyColors(requireContext(), binding.roleBadge)
                }
            }
        }

        binding.birthdayInput.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            picker.addOnPositiveButtonClickListener { selection ->
                userProfile.birthday = selection.toLocalDate()
                binding.birthdayInput.setText(userProfile.birthday.format(dateFormatter))
            }

            picker.show(parentFragmentManager, "date_picker")
        }

        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
        }
        binding.editButton.setOnClickListener {
            userProfile.name = binding.nameInput.text.toString()
            userProfile.phoneNumber = binding.phoneInput.text.toString()

            profileViewModel.updateProfile(userProfile)
            profileViewModel.updateUserName(userProfile.name)

        }
    }
}