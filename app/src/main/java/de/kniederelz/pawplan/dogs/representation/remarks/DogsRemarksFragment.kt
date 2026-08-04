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
import de.kniederelz.pawplan.dogs.representation.overview.adapter.DogsRemarksAdapter
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class DogsRemarksFragment : Fragment() {
    private lateinit var binding: FragmentDogsRemarksBinding

    private val viewModel: DogsRemarksViewModel by viewModels()

    private val overviewAdapter: DogsRemarksAdapter = DogsRemarksAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDogsRemarksBinding.inflate(inflater, container, false)

        binding.remarksCaption.text = getString(R.string.dog_remarks_caption, "")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overviewAdapter.addLoadStateListener { loadState ->
            val isEmpty =
                loadState.refresh is LoadState.NotLoading &&
                        overviewAdapter.itemCount == 0

            binding.noRemarksText.isVisible = isEmpty
            binding.remarksList.isVisible = !isEmpty
        }

        binding.remarksList.adapter = overviewAdapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dog.collect {
                binding.remarksCaption.text = getString(R.string.dog_remarks_caption, it?.name)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.ratings.collect {
                overviewAdapter.submitData(it)
            }
        }
    }
}