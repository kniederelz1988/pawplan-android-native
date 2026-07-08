package de.kniederelz.pawplan.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import de.kniederelz.pawplan.databinding.FragmentTrackerBinding
import de.kniederelz.pawplan.viewModels.TrackerViewModel

class TrackerView : Fragment() {

    companion object {
        fun newInstance() = TrackerView()
    }

    private lateinit var binding: FragmentTrackerBinding

    private val viewModel: TrackerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }
}