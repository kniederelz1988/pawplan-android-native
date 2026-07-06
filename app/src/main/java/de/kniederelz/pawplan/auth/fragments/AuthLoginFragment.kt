package de.kniederelz.pawplan.auth.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import de.kniederelz.pawplan.MainActivity
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentAuthLoginBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class AuthLoginFragment : Fragment() {

    private lateinit var binding: FragmentAuthLoginBinding

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
            Log.d("AuthLoginFragment", "Login button clicked")
            parentFragment?.setFragmentResult("closeSheet", bundleOf())
        }
    }
}