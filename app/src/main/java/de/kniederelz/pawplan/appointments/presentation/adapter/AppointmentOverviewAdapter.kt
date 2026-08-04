package de.kniederelz.pawplan.appointments.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import de.kniederelz.pawplan.R
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import de.kniederelz.pawplan.appointments.repositories.base.domain.AppointmentData
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.timeFormatter
import de.kniederelz.pawplan.core.utils.AppointmentDiff
import de.kniederelz.pawplan.databinding.FragmentAppointmentOverviewItemBinding

class AppointmentOverviewAdapter :
    PagingDataAdapter<AppointmentData, AppointmentOverviewAdapter.AppointmentViewHolder>(AppointmentDiff) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppointmentViewHolder {
        val binding = FragmentAppointmentOverviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: AppointmentViewHolder,
        position: Int
    ) {
        getItem(position)?.let {
            holder.binding.dogNameLabel.text = it.dog.name
            holder.binding.imageView.load(it.dog.imageURL) {
                placeholder(R.drawable.dog_placeholder)
                error(R.drawable.dog_placeholder)
                crossfade(true)
            }

            holder.binding.dateLabel.text = it.appointment.date.format(dateFormatter)
            holder.binding.timeLabel.text = it.appointment.date.format(timeFormatter)
        }
    }

    inner class AppointmentViewHolder(val binding: FragmentAppointmentOverviewItemBinding)
        : RecyclerView.ViewHolder(binding.root)
}
