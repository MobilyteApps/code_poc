package com.android.code.app.ui.home

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * @AUTHOR Amandeep Singh
 */


/**
 *Build camera update  with new position
 * @param lat latitude
 * @param lng longitude
 * @return camera update instance
 */
fun buildCameraUpdate(position: LatLng): CameraUpdate {
    return CameraUpdateFactory.newCameraPosition(
        CameraPosition.Builder()
            .zoom(16.0f)
            .target(position)
            .build()
    )
}

/**
 * Check if selected user location is withing given radius
 * @param rad radius
 * @param origin origin point location i.e user current location
 * @param destination destination point i.e user selected location
 * @return true if selected position is withing given radius else false
 */
fun checkWithingRadius(rad: Int, origin: LatLng, destination: LatLng): Boolean {
    //    val R = 6371 // Radius of the earth km
    val r = 3961 // Radius of the earth miles
    val lat1 = origin.latitude
    val lon1 = origin.longitude
    val lat2 = destination.latitude
    val lon2 = destination.longitude
    val latDistance = toRad(lat2 - lat1)
    val lonDistance = toRad(lon2 - lon1)
    val a = sin(latDistance / 2) * sin(latDistance / 2) + cos(toRad(lat1)) * cos(toRad(lat2)) *
            sin(lonDistance / 2) * sin(lonDistance / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    val distance: Int = (r * c).toInt()
    return distance<= rad
}


private fun toRad(value: Double?): Double {
    return value!! * Math.PI / 180
}