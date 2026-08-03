package de.kniederelz.pawplan.ui.extensions

import android.media.Image
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogGender
import de.kniederelz.pawplan.dogs.domain.DogSize

fun ImageView.setRating(isRated: Boolean) {
    if (isRated) {
        imageTintList = AppCompatResources.getColorStateList(context,R.color.md_theme_outline)
    } else {
        imageTintList = AppCompatResources.getColorStateList(context,R.color.md_theme_outlineVariant)
    }
}

fun ImageView.setGender(gender: DogGender) {
    when (gender) {
        DogGender.FEMALE -> {
            setImageResource(R.drawable.ic_gender_female)
            contentDescription = context.getString(R.string.dog_gender_female)
        }
        DogGender.FEMALE_NEUTERED -> {
            setImageResource(R.drawable.ic_gender_female_neutered)
            contentDescription = context.getString(R.string.dog_gender_female_neutered)
        }
        DogGender.MALE -> {
            setImageResource(R.drawable.ic_gender_male)
            contentDescription = context.getString(R.string.dog_gender_male)
        }
        DogGender.MALE_NEUTERED -> {
            setImageResource(R.drawable.ic_gender_male_neutered)
            contentDescription = context.getString(R.string.dog_gender_male_neutered)
        }
    }
}
fun ImageView.setSize(size: DogSize) {
    when (size) {
        DogSize.SMALL -> {
            setImageResource(R.drawable.ic_size_small)
            contentDescription = context.getString(R.string.dog_size_small)
        }

        DogSize.MEDIUM -> {
            setImageResource(R.drawable.ic_size_medium)
            contentDescription = context.getString(R.string.dog_size_medium)
        }

        DogSize.LARGE -> {
            setImageResource(R.drawable.ic_size_large)
            contentDescription = context.getString(R.string.dog_size_large)
        }
    }
}

