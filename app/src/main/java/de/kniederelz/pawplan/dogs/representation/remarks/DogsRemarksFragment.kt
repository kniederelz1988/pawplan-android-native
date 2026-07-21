package de.kniederelz.pawplan.dogs.representation.remarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.kniederelz.pawplan.databinding.FragmentDogsRemarksBinding

class DogsRemarksFragment : Fragment() {
    private lateinit var binding: FragmentDogsRemarksBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDogsRemarksBinding.inflate(inflater, container, false)
        return binding.root
    }
}