package de.kniederelz.pawplan.ui.extensions

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.appointments.repositories.ratings.domain.AppointmentRatingStatistics
import de.kniederelz.pawplan.appointments.repositories.status.domain.AppointmentStatusType
import de.kniederelz.pawplan.dogs.domain.DogAge
import de.kniederelz.pawplan.user.domain.UserRoleType

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

fun TextView.applyRole(context: Context, role: UserRoleType) {
    text = when(role) {
        UserRoleType.ADMIN -> context.getString(R.string.profile_role_admin)
        UserRoleType.VOLUNTEER -> context.getString(R.string.profile_role_volunteer)
        else -> context.getString(R.string.profile_role_observer)
    }

    val drawable = background as? GradientDrawable
        ?: return

    when(role) {
        UserRoleType.ADMIN -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_errorContainer)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_error)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onErrorContainer)
            setTextColor(textColor)
        }
        UserRoleType.VOLUNTEER -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_primaryContainer)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_primary)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onPrimaryContainer)
            setTextColor(textColor)
        }
        else -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_background)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_outline)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onBackground)
            setTextColor(textColor)
        }
    }
}
fun TextView.applyStatus(context: Context, status: AppointmentStatusType) {
    text = when(status) {
        AppointmentStatusType.PENDING -> context.getString(R.string.appointment_badge_pending)
        AppointmentStatusType.CONFIRMED -> context.getString(R.string.appointment_badge_confirmed)
        AppointmentStatusType.CANCELLED -> context.getString(R.string.appointment_badge_cancelled)
        else -> context.getString(R.string.appointment_badge_completed)
    }

    val drawable = background as? GradientDrawable
        ?: return

    when(status) {
        AppointmentStatusType.PENDING -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_background)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_outline)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onBackground)
            setTextColor(textColor)
        }
        AppointmentStatusType.CONFIRMED -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_primaryContainer)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_primary)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onPrimaryContainer)
            setTextColor(textColor)
        }
        AppointmentStatusType.CANCELLED -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_errorContainer)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_error)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onErrorContainer)
            setTextColor(textColor)
        }
        else -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_secondaryContainer)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_secondary)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onSecondaryContainer)
            setTextColor(textColor)
        }
    }
}