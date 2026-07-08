package de.kniederelz.pawplan.views

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.kniederelz.pawplan.databinding.FragmentProfileBinding
import de.kniederelz.pawplan.viewModels.ProfileViewViewModel

class ProfileView : Fragment() {

    companion object {
        fun newInstance() = ProfileView()
    }

    private lateinit var binding: FragmentProfileBinding

    private val viewModel: ProfileViewViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
}