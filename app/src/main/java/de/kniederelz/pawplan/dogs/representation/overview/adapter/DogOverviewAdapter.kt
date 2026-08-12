package de.kniederelz.pawplan.dogs.representation.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.databinding.FragmentDogsOverviewItemBinding
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.extensions.getAge
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import de.kniederelz.pawplan.core.ui.extensions.setAge
import de.kniederelz.pawplan.core.ui.extensions.setFavorite
import de.kniederelz.pawplan.core.ui.extensions.setGender
import de.kniederelz.pawplan.core.ui.extensions.setSize
import de.kniederelz.pawplan.dogs.representation.overview.DogOverviewData
import de.kniederelz.pawplan.core.ui.extensions.setStatisticsAverage
import de.kniederelz.pawplan.core.ui.extensions.setStatisticsCount

class DogOverviewAdapter(
    private val dogOverviewData: List<DogOverviewData>,
    private val onDogButtonSubmit: (Dog) -> Unit,
    private val onDogLikeButtonSubmit: (Dog) -> Unit
) : RecyclerView.Adapter<DogOverviewAdapter.DogViewHolder>() {

    override fun getItemCount(): Int {
        return dogOverviewData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = FragmentDogsOverviewItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )

        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        dogOverviewData[position].let {
            holder.binding.mainLayout.setOnClickListener { _ ->
                onDogButtonSubmit(it.dog)
            }
            holder.binding.favButton.setFavorite(it.isFavorite)
            holder.binding.favButton.setOnClickListener { _ ->
                onDogLikeButtonSubmit(it.dog)
            }

            holder.binding.nameLabel.text = it.dog.name

            holder.binding.ratingLabel.setStatisticsAverage(it.statistics)
            holder.binding.ratingCount.setStatisticsCount(it.statistics)

            holder.binding.ageLabel.setAge(it.dog.getAge())
            holder.binding.genderImage.setGender(it.dog.gender)
            holder.binding.sizeImage.setSize(it.dog.size)

            holder.binding.imageView.load(it.dog.imageURL) {
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

    class DogViewHolder(val binding: FragmentDogsOverviewItemBinding)
        : RecyclerView.ViewHolder(binding.root)
}
