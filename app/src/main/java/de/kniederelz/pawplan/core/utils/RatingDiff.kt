package de.kniederelz.pawplan.core.utils

import androidx.recyclerview.widget.DiffUtil
import de.kniederelz.pawplan.dogs.representation.remarks.AppointmentRatingData

object RatingDiff : DiffUtil.ItemCallback<AppointmentRatingData>() {
    override fun areItemsTheSame(
        oldItem: AppointmentRatingData,
        newItem: AppointmentRatingData
    ) = oldItem.rating.id == newItem.rating.id

    override fun areContentsTheSame(
        oldItem: AppointmentRatingData,
        newItem: AppointmentRatingData
    ) = oldItem == newItem
}