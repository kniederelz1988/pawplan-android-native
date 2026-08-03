package de.kniederelz.pawplan.dogs.representation.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRating
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.utils.RatingDiff
import de.kniederelz.pawplan.databinding.FragmentDogRemarkItemBinding
import de.kniederelz.pawplan.ui.extensions.setRating

class DogsRemarksAdapter : PagingDataAdapter<AppointmentRating, DogsRemarksAdapter.RatingsViewHolder>(
    RatingDiff
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingsViewHolder {
        val binding = FragmentDogRemarkItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RatingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingsViewHolder, position: Int) {
        getItem(position)?.let {
            holder.binding.remarkNameLabel.text = it.volunteerName
            holder.binding.remarkDateLabel.text = it.updatedAt.format(dateFormatter)
            holder.binding.remarkLabel.text = it.comment

            holder.binding.ratingStar1Image.setRating(it.rating >= 1)
            holder.binding.ratingStar2Image.setRating(it.rating >= 2)
            holder.binding.ratingStar3Image.setRating(it.rating >= 3)
            holder.binding.ratingStar4Image.setRating(it.rating >= 4)
            holder.binding.ratingStar5Image.setRating(it.rating >= 5)
        }
    }

    inner class RatingsViewHolder(val binding: FragmentDogRemarkItemBinding)
        : RecyclerView.ViewHolder(binding.root)
}

