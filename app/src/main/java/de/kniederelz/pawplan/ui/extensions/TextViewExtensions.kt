package de.kniederelz.pawplan.ui.extensions

import android.widget.TextView
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.appointmentratings.domain.AppointmentRatingStatistics
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

fun TextView.setStatisticsAverage(statistics: AppointmentRatingStatistics?) {
    text = context.getString(
        R.string.dog_rating_average,
        statistics?.average ?: 0f
    )
}
fun TextView.setStatisticsCount(statistics: AppointmentRatingStatistics?) {
    text = context.getString(
        R.string.dog_rating_count,
        statistics?.count ?: 0
    )
}