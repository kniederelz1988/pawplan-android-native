package de.kniederelz.pawplan.dogs.representation.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.core.utils.DogDiff
import de.kniederelz.pawplan.databinding.FragmentDogListItemBinding
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.getAge
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import de.kniederelz.pawplan.ui.extensions.setAge
import de.kniederelz.pawplan.ui.extensions.setFavorite

class DogOverviewAdapter(
    private val onDogButtonSubmit: (Dog) -> Unit,
    private val onDogLikeButtonSubmit: (Dog) -> Unit
) : PagingDataAdapter<Dog, DogOverviewAdapter.DogViewHolder>(DogDiff) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DogViewHolder {
        val binding = FragmentDogListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: DogViewHolder,
        position: Int
    ) {
        getItem(position)?.let {
            holder.binding.mainLayout.setOnClickListener { _ ->
                onDogButtonSubmit(it)
            }
            holder.binding.favButton.setFavorite(it.isFavorite)
            holder.binding.favButton.setOnClickListener { _ ->
                onDogLikeButtonSubmit(it)
            }

            holder.binding.nameLabel.text = it.name

            holder.binding.ageLabel.setAge(it.getAge())

            holder.binding.genderLabel.text = it.gender.toString()
            holder.binding.sizeLabel.text = it.size.toString()

            holder.binding.imageView.load(it.imageURL) {
                placeholder(R.drawable.dog_placeholder)
                error(R.drawable.dog_placeholder)
                crossfade(true)
            }
        }
    }

    override fun onViewRecycled(holder: DogViewHolder) {
        super.onViewRecycled(holder)

        holder.binding.mainLayout.setOnClickListener(null)
        holder.binding.favButton.setOnClickListener(null)
    }

    inner class DogViewHolder(val binding: FragmentDogListItemBinding)
        : RecyclerView.ViewHolder(binding.root)
}

