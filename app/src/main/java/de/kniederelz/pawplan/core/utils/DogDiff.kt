package de.kniederelz.pawplan.core.utils

import androidx.recyclerview.widget.DiffUtil
import de.kniederelz.pawplan.dogs.domain.Dog

object DogDiff : DiffUtil.ItemCallback<Dog>() {
    override fun areItemsTheSame(oldItem: Dog, newItem: Dog) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Dog, newItem: Dog) = oldItem == newItem
}