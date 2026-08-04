package de.kniederelz.pawplan.user.presentation

import android.os.Bundle
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

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                profileViewModel.userProfile.collect { profile ->
                    val curProfile = profile ?: UserProfile()

                    birthday = curProfile.birthday

                    binding.initialsText.text = curProfile.name.initials()

                    binding.nameText.text = curProfile.name
                    binding.nameInput.setText(curProfile.name)

                    binding.phoneInput.setText(curProfile.phoneNumber)
                    binding.birthdayInput.setText(curProfile.birthday.format(dateFormatter))
                    binding.volunteerSinceInput.setText(curProfile.volunteerSince.format(dateFormatter))
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
                birthday = selection.toLocalDate()
                binding.birthdayInput.setText(birthday.format(dateFormatter))
            }

            picker.show(parentFragmentManager, "date_picker")
        }

        binding.logoutButton.setOnClickListener {
            authViewModel.logout()
        }
        binding.editButton.setOnClickListener {
            val curProfile = profileViewModel.userProfile.value
                ?: return@setOnClickListener

            val userProfile = UserProfile(
                curProfile.id,
                curProfile.userId,
                binding.nameInput.text.toString(),
                binding.phoneInput.text.toString(),
                birthday,
                curProfile.volunteerSince
            )

            profileViewModel.updateProfile(userProfile)
            profileViewModel.updateUserName(userProfile.name)
        }
    }
}