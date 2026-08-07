package de.kniederelz.pawplan.tracking.presentation.overview.adapter

import android.view.View
import androidx.core.content.ContextCompat.getString
import de.kniederelz.pawplan.core.extensions.dateFormatter
import de.kniederelz.pawplan.core.extensions.timeFormatter
import de.kniederelz.pawplan.core.ui.extensions.setRating
import de.kniederelz.pawplan.databinding.FragmentTrackerAppointmentBinding
import de.kniederelz.pawplan.tracking.repositories.TrackerAppointmentData
import de.kniederelz.pawplan.tracking.repositories.session.domain.getDistance
import java.time.Duration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.kniederelz.pawplan.R
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
class TrackerAppointmentAdapter(
    private val trackerAppointmentData: List<TrackerAppointmentData>
) : RecyclerView.Adapter<TrackerAppointmentAdapter.AppointmentViewHolder>() {

    override fun getItemCount(): Int {
        return trackerAppointmentData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int
    ): AppointmentViewHolder {
        val binding = FragmentTrackerAppointmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        trackerAppointmentData[position].let { appointmentData ->
            holder.binding.dogNameLabel.text = appointmentData.dog.name
            holder.binding.dogImageView.load(appointmentData.dog.imageURL) {
                placeholder(R.drawable.dog_placeholder)
                error(R.drawable.dog_placeholder)
                crossfade(true)
            }

            holder.binding.dateLabel.text = appointmentData.appointment.date.format(dateFormatter)
            holder.binding.timeLabel.text = appointmentData.appointment.date.format(timeFormatter)

            holder.binding.ratingLayout.visibility = View.INVISIBLE

            appointmentData.appointmentRating?.let {
                holder.binding.ratingLayout.visibility = View.VISIBLE

                holder.binding.ratingStar1Image.setRating(it.rating >= 1)
                holder.binding.ratingStar2Image.setRating(it.rating >= 2)
                holder.binding.ratingStar3Image.setRating(it.rating >= 3)
                holder.binding.ratingStar4Image.setRating(it.rating >= 4)
                holder.binding.ratingStar5Image.setRating(it.rating == 5)
            }

            holder.binding.sessionLengthLabel.text = holder.itemView.context.getString(
                R.string.wtp_walklength, 0f
            )
            holder.binding.sessionDurationLabel.text = holder.itemView.context.getString(
                R.string.wtp_walkduration, 0L, 0L
            )

            appointmentData.trackingSession?.let {
                holder.binding.sessionLengthLabel.text = holder.itemView.context.getString(
                    R.string.wtp_walklength,
                    it.locations.getDistance() / 1000
                )

                val duration = Duration.between(it.startTimestamp, it.endTimestamp)
                holder.binding.sessionDurationLabel.text = holder.itemView.context.getString(
                    R.string.wtp_walkduration,
                    duration.toHours(),
                    duration.toMinutes() % 60
                )
            }
        }
    }

    inner class AppointmentViewHolder(val binding: FragmentTrackerAppointmentBinding)
        : RecyclerView.ViewHolder(binding.root)
}
