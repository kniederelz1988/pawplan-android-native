package de.kniederelz.pawplan.appointments.presentation.overview.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.kniederelz.pawplan.R
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import de.kniederelz.pawplan.appointments.repositories.AppointmentData
import de.kniederelz.pawplan.appointments.repositories.base.domain.canStart
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.timeFormatter
import de.kniederelz.pawplan.databinding.FragmentAppointmentOverviewItemBinding
import de.kniederelz.pawplan.ui.extensions.applyStatus

class AppointmentOverviewAdapter(
    private val appointmentData: List<AppointmentData>,
    private val onStartButtonSubmit: (AppointmentData) -> Unit,
    private val onEditButtonSubmit: (AppointmentData) -> Unit,
    private val onCancelButtonSubmit: (AppointmentData) -> Unit,
) : RecyclerView.Adapter<AppointmentOverviewAdapter.AppointmentViewHolder>() {

    override fun getItemCount(): Int {
        return appointmentData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = FragmentAppointmentOverviewItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        appointmentData[position].let { data ->
            holder.binding.dogNameLabel.text = data.dog.name
            holder.binding.imageView.load(data.dog.imageURL) {
                placeholder(R.drawable.dog_placeholder)
                error(R.drawable.dog_placeholder)
                crossfade(true)
            }

            holder.binding.dateLabel.text = data.appointment.date.format(dateFormatter)
            holder.binding.timeLabel.text = data.appointment.date.format(timeFormatter)

            holder.binding.statusBadge.applyStatus(holder.binding.root.context, data.appointmentStatus.status)

            holder.binding.editButton.visibility = View.GONE
            holder.binding.editButton.setOnClickListener {
                onEditButtonSubmit(data)
            }

            holder.binding.startButton.visibility = View.GONE
            holder.binding.startButton.isEnabled = data.appointment.canStart()
            holder.binding.startButton.setOnClickListener {
                onStartButtonSubmit(data)
            }

            holder.binding.cancelButton.visibility =
                if (data.appointmentStatus.status in setOf(
                        AppointmentStatusType.PENDING,
                        AppointmentStatusType.CONFIRMED
                ))
                    View.VISIBLE
                else
                    View.GONE

            holder.binding.cancelButton.setOnClickListener {
                onCancelButtonSubmit(data)
            }
        }
    }

    override fun onViewRecycled(holder: AppointmentViewHolder) {
        super.onViewRecycled(holder)

        holder.binding.startButton.setOnClickListener(null)
        holder.binding.editButton.setOnClickListener(null)
        holder.binding.cancelButton.setOnClickListener(null)
    }

    inner class AppointmentViewHolder(val binding: FragmentAppointmentOverviewItemBinding)
        : RecyclerView.ViewHolder(binding.root)
}

