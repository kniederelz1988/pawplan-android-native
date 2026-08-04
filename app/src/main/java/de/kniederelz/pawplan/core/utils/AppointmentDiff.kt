package de.kniederelz.pawplan.core.utils

import androidx.recyclerview.widget.DiffUtil
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentData

object AppointmentDiff : DiffUtil.ItemCallback<AppointmentData>() {
    override fun areItemsTheSame(oldItem: AppointmentData, newItem: AppointmentData) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: AppointmentData, newItem: AppointmentData) = oldItem == newItem
}