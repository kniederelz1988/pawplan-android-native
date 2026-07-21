package de.kniederelz.pawplan.core.extensions

import de.kniederelz.pawplan.R

fun String.initials(): String {
    val words = trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }

    return when (words.size) {
        0 -> R.string.profile_initial_text.toString()
        1 -> words.first().first().uppercase()
        else -> "${words.first().first()}${words.last().first()}".uppercase()
    }
}