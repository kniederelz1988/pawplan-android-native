package de.kniederelz.pawplan.core.ui.extensions

import org.osmdroid.util.BoundingBox
import org.osmdroid.views.MapView

fun MapView.setBoundingBox(boundingBox: BoundingBox?) {
    if (boundingBox == null)
        return

    val initPos = mapCenter
    val initZoom = zoomLevelDouble

    zoomToBoundingBox(
        boundingBox,
        false,
        40
    )

    val targetPos = mapCenter
    val targetZoom = zoomLevelDouble.coerceAtMost(20.0)

    controller.setCenter(initPos)
    controller.setZoom(initZoom)

    controller.animateTo(
        targetPos,
        targetZoom,
        1000L
    )
}