package com.android.code.app.ui.home

import com.android.code.app.base.CommonViewActor
import com.android.code.app.utils.LocationHandlerListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.android.code.app.utils.Constants
import eu.amirs.JSON

/**
 * @AUTHOR Amandeep Singh
 */
interface HomeViewActor : CommonViewActor, OnMapReadyCallback, LocationHandlerListener {

    /**
     *Add marker on map with given position
     * @param coordinates position of marker
     * @param title title for marker
     */
    fun addMarker(coordinates: LatLng, title: String)

    /**
     *Update camera position
     * @param coordinates new location
     * @param duration duration of animation
     */
    fun updateMapCamera(coordinates: LatLng, duration: Int)

    /**
     *Move user map to current location
     */
    fun goToCurrentLocation()

    /**
     *Show radius dialog if user selected place is withing give radius
     * For radius value see
     * @see Constants.RADIUS_LIMIT
     */
    fun showWithingRadiusDialog()

    /**
     *Show error message
     * @param t throwable
     */
    fun onErrorThrowable(t:Throwable)

    /**
     *Do something with
     * @param result result from server
     */
    fun doWithSocketData(result: JSON)


}