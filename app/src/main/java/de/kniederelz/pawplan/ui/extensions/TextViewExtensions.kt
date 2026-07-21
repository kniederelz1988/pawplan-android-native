package de.kniederelz.pawplan.ui.extensions

import android.widget.TextView
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.dogs.domain.DogAge

fun TextView.setAge(age: DogAge) {
    text =
        if (age.years > 0) {
            resources.getQuantityString(
                R.plurals.dog_age_years,
                age.years,
                age.years
            )
        } else {
            resources.getQuantityString(
                R.plurals.dog_age_months,
                age.months,
                age.months
            )
        }
}