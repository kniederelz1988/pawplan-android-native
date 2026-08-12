package de.kniederelz.pawplan.core.ui.extensions

import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.button.MaterialButton
import de.kniederelz.pawplan.R

fun MaterialButton.setFavorite(isFavorite: Boolean) {
    if (isFavorite) {
        icon = AppCompatResources.getDrawable(context,R.drawable.ic_favorite_full)
        contentDescription = context.getString(R.string.dog_details_fav_button)
    } else {
        icon = AppCompatResources.getDrawable(context,R.drawable.ic_favortie_outline)
        contentDescription = context.getString(R.string.dog_details_defav_button)
    }
}