package de.kniederelz.pawplan.fragments

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
import de.kniederelz.pawplan.databinding.FragmentAuthRegisterBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AuthRegisterFragment : Fragment() {
    private lateinit var binding: FragmentAuthRegisterBinding

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
            Log.d("AuthRegisterFragment", "Register button clicked")
            parentFragment?.setFragmentResult("closeSheet", bundleOf())
        }
    }
}