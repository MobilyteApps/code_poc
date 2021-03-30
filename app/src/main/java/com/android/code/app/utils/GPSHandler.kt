package com.android.code.app.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task


/**
 * @AUTHOR Amandeep Singh
 */

class GPSHandler(var listener: LocationHandlerListener) {
    companion object {
        const val REQUEST_CODE_GPS_ENABLE = 1021
        const val REQUEST_CODE_LOCATION = 1022
        var STICK_TO_CURRENT_LOCATION = true
    }

    private var mLastLocation: Location? = null
    val locationUpdate = MutableLiveData<LatLng>()
    val currentLocationAvailable = MutableLiveData<LatLng>()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var context: Context
    private val locationManager by lazy { context.getSystemService(Context.LOCATION_SERVICE) as LocationManager }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            mLastLocation = locationResult?.lastLocation
            if (mLastLocation != null) {
                if (STICK_TO_CURRENT_LOCATION) {
                    currentLocationAvailable.postValue(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))

                }
                locationUpdate.postValue(LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude))
            }
        }
    }

    fun stopLocationUpdates() {
        mFusedLocationClient?.removeLocationUpdates(locationCallback)
    }

    fun startLocationUpdate() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        mFusedLocationClient?.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper())
    }

    fun checkPermissionFrag(fragment: Fragment) {
        context = fragment.context!!
        listener.checkingPermission()
        if (ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context,
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(fragment)
        } else {
            listener.permissionGranted()
            turnOnGPS()
        }
    }

    private fun requestPermission(fragment: Fragment) {
        listener.askingPermission()
        fragment.requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
    }


    private fun turnOnGPS() {
        listener.checkingGPS()
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            listener.GPSTurnedOn()
        } else {
            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(createLocationRequest())
            builder.setAlwaysShow(true)

            val client: SettingsClient = LocationServices.getSettingsClient(context)
            val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

            task.addOnSuccessListener { _ ->
                listener.GPSTurnedOn()
            }

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    listener.turningOnGPS()
                    try {
                        exception.startResolutionForResult(context as Activity,
                                REQUEST_CODE_GPS_ENABLE)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                } else {
                    listener.onError("Unable to turn on Gps")
                }
            }
        }
    }

    private fun createLocationRequest(): LocationRequest {
        return LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }!!
    }

    fun handlerResult() {
        listener.GPSTurnedOn()
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (permissions.isEmpty() || grantResults.isEmpty()) {
            return
        }
        if (requestCode == REQUEST_CODE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listener.permissionGranted()
                turnOnGPS()
            } else {
                listener.permissionDenied()
            }
        }
    }
}


interface LocationHandlerListener {
    fun checkingPermission()
    fun askingPermission()
    fun permissionGranted()
    fun permissionDenied()
    fun checkingGPS()
    fun turningOnGPS()
    fun GPSTurnedOn()
    fun onError(message: String)


}
