package de.kniederelz.pawplan.dogs.representation.remarks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.databinding.FragmentDogsRemarkItemBinding
import de.kniederelz.pawplan.core.ui.extensions.setRating
import de.kniederelz.pawplan.dogs.representation.remarks.AppointmentRatingData

class DogsRemarksAdapter(
    private val remarks: List<AppointmentRatingData>
) : RecyclerView.Adapter<DogsRemarksAdapter.RatingsViewHolder>() {

    override fun getItemCount(): Int {
        return remarks.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatingsViewHolder {
        val binding = FragmentDogsRemarkItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return RatingsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RatingsViewHolder, position: Int) {
        remarks[position].let { data ->
            holder.binding.remarkNameLabel.text = data.userProfile.name
            holder.binding.remarkDateLabel.text = data.rating.updateAt.format(dateFormatter)
            holder.binding.remarkLabel.text = data.rating.comment

            holder.binding.ratingStar1Image.setRating(data.rating.rating >= 1)
            holder.binding.ratingStar2Image.setRating(data.rating.rating >= 2)
            holder.binding.ratingStar3Image.setRating(data.rating.rating >= 3)
            holder.binding.ratingStar4Image.setRating(data.rating.rating >= 4)
            holder.binding.ratingStar5Image.setRating(data.rating.rating >= 5)
        }
    }

    class RatingsViewHolder(val binding: FragmentDogsRemarkItemBinding)
        : RecyclerView.ViewHolder(binding.root)
}

