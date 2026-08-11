package de.kniederelz.pawplan.appointments.presentation.overview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.kniederelz.pawplan.R
import coil3.load
import coil3.request.crossfade
import coil3.request.error
import coil3.request.placeholder
import de.kniederelz.pawplan.databinding.FragmentAppointmentFastselectItemBinding
import de.kniederelz.pawplan.dogs.domain.Dog

class AppointmentFastSelectionAdapter(
    private val fastSelectDogs: List<Dog>,
    private val onItemSubmit: (Dog) -> Unit,
): RecyclerView.Adapter<AppointmentFastSelectionAdapter.AppointmentViewHolder>() {

    override fun getItemCount(): Int {
        return fastSelectDogs.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = FragmentAppointmentFastselectItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        fastSelectDogs[position].let { dog ->
            holder.binding.fastSelectName.text = dog.name
            holder.binding.fastSelectImage.load(dog.imageURL) {
                placeholder(R.drawable.dog_placeholder)
                error(R.drawable.dog_placeholder)
                crossfade(true)
            }

            holder.binding.fastSelectImage.setOnClickListener {
                onItemSubmit(dog)
            }
        }
    }

    override fun onViewRecycled(holder: AppointmentViewHolder) {
        super.onViewRecycled(holder)

        holder.binding.fastSelectImage.setOnClickListener(null)
    }

    inner class AppointmentViewHolder(val binding: FragmentAppointmentFastselectItemBinding)
        : RecyclerView.ViewHolder(binding.root)
}
