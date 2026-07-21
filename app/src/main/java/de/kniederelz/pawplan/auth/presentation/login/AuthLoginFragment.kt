package de.kniederelz.pawplan.auth.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.auth.AuthViewModel
import de.kniederelz.pawplan.databinding.FragmentAuthLoginBinding

/**
 * A simple [androidx.fragment.app.Fragment] subclass as the default destination in the navigation.
 */

@AndroidEntryPoint
class AuthLoginFragment : Fragment() {
    private lateinit var binding: FragmentAuthLoginBinding

    private val authViewModel: AuthViewModel by activityViewModels()
    private val loginViewModel: AuthLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            findNavController().navigate(R.id.action_Login_To_Register)
        }
        binding.loginButton.setOnClickListener {
            val email = binding.loginUserNameInput.text.toString()
            val password = binding.loginPasswordInput.text.toString()

            authViewModel.login(email, password)
        }
    }
}