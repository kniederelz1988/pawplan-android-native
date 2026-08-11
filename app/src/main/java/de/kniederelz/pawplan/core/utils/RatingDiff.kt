package de.kniederelz.pawplan.core.utils

import androidx.recyclerview.widget.DiffUtil
import de.kniederelz.pawplan.dogs.representation.remarks.DogsRemarksViewModel

object RatingDiff : DiffUtil.ItemCallback<DogsRemarksViewModel.AppointmentRatingData>() {
    override fun areItemsTheSame(
        oldItem: DogsRemarksViewModel.AppointmentRatingData,
        newItem: DogsRemarksViewModel.AppointmentRatingData
    ) = oldItem.rating.id == newItem.rating.id

    override fun areContentsTheSame(
        oldItem: DogsRemarksViewModel.AppointmentRatingData,
        newItem: DogsRemarksViewModel.AppointmentRatingData
    ) = oldItem == newItem
}