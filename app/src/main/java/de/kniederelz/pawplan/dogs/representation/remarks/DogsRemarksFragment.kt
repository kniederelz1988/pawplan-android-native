package de.kniederelz.pawplan.dogs.representation.remarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentDogsRemarksBinding
import de.kniederelz.pawplan.dogs.representation.remarks.adapter.DogsRemarksAdapter
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class DogsRemarksFragment : Fragment() {
    private lateinit var binding: FragmentDogsRemarksBinding

    private val viewModel: DogsRemarksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDogsRemarksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.dog.observe(viewLifecycleOwner) {
            binding.remarksCaption.text = getString(R.string.dog_remarks_caption, it?.name)
        }
        viewModel.ratings.observe(viewLifecycleOwner) {
            binding.noRemarksText.isVisible = it.isEmpty()
            binding.remarksList.isVisible = it.isNotEmpty()

            binding.remarksList.adapter = DogsRemarksAdapter(it)
        }
    }
}