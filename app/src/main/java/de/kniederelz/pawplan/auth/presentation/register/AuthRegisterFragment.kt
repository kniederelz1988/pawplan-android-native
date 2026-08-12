package de.kniederelz.pawplan.auth.presentation.register

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.auth.AuthViewModel
import de.kniederelz.pawplan.databinding.FragmentAuthRegisterBinding
import kotlinx.coroutines.launch
import kotlin.getValue

/**
 * A simple [androidx.fragment.app.Fragment] subclass as the second destination in the navigation.
 */
class AuthRegisterFragment : Fragment() {
    private lateinit var binding: FragmentAuthRegisterBinding

    private val authViewModel: AuthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.setOnClickListener {
            findNavController().navigate(R.id.action_Register_To_Login)
        }
        binding.registerButton.setOnClickListener {
            val email = binding.registerUserNameInput.text.toString()
            val password = binding.registerPasswordInput.text.toString()
            val name = binding.registerDisplayNameInput.text.toString()

            lifecycleScope.launch {
                authViewModel.register(email, password, name)
            }
        }
    }
}