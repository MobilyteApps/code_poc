package com.android.code.app.services.location

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.android.code.app.data.remote.socket.SocketHandler
import com.android.code.app.ui.dashboard.DashboardActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng


class LocationService : LifecycleService() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var mNotificationManager: NotificationManager

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            //Decide how to use/store location coordinates
            Log.d("New Coordinates", locationResult.lastLocation.toString())
            SocketHandler.instance.sendLocation(LatLng(locationResult.lastLocation.latitude,locationResult.lastLocation.longitude))
        }
    }


    override fun onCreate() {
        super.onCreate()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().apply {
            interval = 5 * DateUtils.SECOND_IN_MILLIS
            priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        }

    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        showNotification()
        registerForLocationTracking()
        //Mainly because we want Service to restart if user revokes permission and to notify him
        return Service.START_STICKY
    }


    /**
     * We only start listening when Gps and Location Permission are enabled
     */
    private fun registerForLocationTracking() {
        isTrackingRunning = try {
            mFusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback,
                Looper.myLooper()
            )
            true
        } catch (unlikely: SecurityException) {
            error("Error when registerLocationUpdates()")
        }

    }

    private fun unregisterFromLocationTracking() {
        try {
            mFusedLocationClient.removeLocationUpdates(locationCallback)
        } catch (unlikely: SecurityException) {
            error("Error when unregisterLocationUpdated()")
        }
    }


    private fun showNotification() {
        isServiceRunning = true
        Intent(this, DashboardActivity::class.java)
            .let { PendingIntent.getActivity(this, 0, it, 0) }
            .let { pendingIntent ->
                createOngoingNotificationChannel()
                startForeground(
                    12345,
                    NotificationCompat.Builder(this, "Code_Android")
                        .setContentTitle("Fetching Location")
                        .setOngoing(true)
                        .setContentText("Getting background location")
                        .setSmallIcon(com.android.code.app.R.mipmap.ic_launcher_round)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                        .build()
                )
            }

    }


    private fun createOngoingNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                "Code_Android", "Code",
                NotificationManager.IMPORTANCE_MIN
            )
                .let { channel ->
                    mNotificationManager.createNotificationChannel(channel)
                }
        }
    }


    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        isTrackingRunning = false
        isServiceRunning = false

    }


    private fun stopServiceIfNeeded() {
        stopSelf()

    }

    companion object {
        //Refers to when this service is running and foreground notification is being displayed
        var isServiceRunning: Boolean = false
            private set

        //Refers to when app is listening to location updates
        var isTrackingRunning: Boolean = false
            private set
    }
}