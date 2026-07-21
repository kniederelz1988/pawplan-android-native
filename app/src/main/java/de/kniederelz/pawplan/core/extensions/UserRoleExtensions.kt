package de.kniederelz.pawplan.core.extensions

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import de.kniederelz.pawplan.R
import de.kniederelz.pawplan.user.domain.UserRole

fun UserRole.toRoleString(): String {
    when(this) {
        UserRole.ADMIN -> return "Admin"
        UserRole.VOLUNTEER -> return "Volunteer"
        else -> return "Observer"
    }
}

fun UserRole.applyColors(context: Context, textView: TextView) {
    val drawable = textView.background as GradientDrawable

    when(this) {
        UserRole.ADMIN -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_errorContainer)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_error)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onErrorContainer)
            textView.setTextColor(textColor)
        }
        UserRole.VOLUNTEER -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_primaryContainer)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_primary)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onPrimaryContainer)
            textView.setTextColor(textColor)
        }
        else -> {
            val backgroundColor = ContextCompat.getColor(context, R.color.md_theme_background)
            drawable.setColor(backgroundColor)

            val strokeWidth = (2 * context.resources.displayMetrics.density).toInt()
            val strokeColor = ContextCompat.getColor(context, R.color.md_theme_outline)
            drawable.setStroke(strokeWidth, strokeColor)

            val textColor = ContextCompat.getColor(context, R.color.md_theme_onBackground)
            textView.setTextColor(textColor)
        }
    }
}