package de.kniederelz.pawplan.core.ui.extensions

import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

fun MapView.showPosition(latitude: Double, longitude: Double, zoom: Double) {
    controller.setZoom(zoom)
    controller.setCenter(GeoPoint(latitude, longitude))
}