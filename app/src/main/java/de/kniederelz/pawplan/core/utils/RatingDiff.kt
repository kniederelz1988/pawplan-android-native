package de.kniederelz.pawplan.core.utils

import androidx.recyclerview.widget.DiffUtil
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRating

object RatingDiff : DiffUtil.ItemCallback<AppointmentRating>() {
    override fun areItemsTheSame(
        oldItem: AppointmentRating,
        newItem: AppointmentRating
    ) = oldItem.id == newItem.id

    override fun areContentsTheSame(
        oldItem: AppointmentRating,
        newItem: AppointmentRating
    ) = oldItem == newItem
}