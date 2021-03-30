package com.android.code.app.ui.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.android.code.app.BR
import com.android.code.app.R
import com.android.code.app.base.BaseFragment
import com.android.code.app.databinding.FragmentHomeBinding
import com.android.code.app.services.location.LocationService
import com.android.code.app.services.location.LocationService.Companion.isServiceRunning
import com.android.code.app.services.location.LocationService.Companion.isTrackingRunning
import com.android.code.app.services.location.ServiceManager
import com.android.code.app.ui.dashboard.DashboardActivity
import com.android.code.app.utils.Constants
import com.android.code.app.utils.Constants.CAMERA_UPDATE_DURATION
import com.android.code.app.utils.GPSHandler
import com.android.code.app.utils.GPSHandler.Companion.REQUEST_CODE_GPS_ENABLE
import com.android.code.app.utils.getViewModelFactory
import com.android.code.app.utils.showToast
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import eu.amirs.JSON


/**
 * @AUTHOR Amandeep Singh
 */
class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(),
    HomeViewActor {
    //region Variables
    private val mViewModel by viewModels<HomeViewModel> { getViewModelFactory() }
    private var mBinding: FragmentHomeBinding? = null
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mLocationHandler: GPSHandler
     var mUserCurrentCoordinates: LatLng? = null
    private val mServiceManager by lazy {
        ServiceManager(
            baseActivity!!,
            Intent(baseActivity!!, LocationService::class.java)
        )
    }
    //endregion


    //region Base Class Methods
    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.fragment_home

    override val viewModel: HomeViewModel
        get() = mViewModel
    //endregion

    //region Life Cycle Method
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set view actor
        mViewModel.setViewActor(this)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //set binding variables
        mBinding = viewDataBinding
        super.onViewCreated(view, savedInstanceState)
        //start loader
        mViewModel.getLoading().postValue(true)
        // [START gps_and_location_permission_handle]
        mLocationHandler = GPSHandler(this)
        mLocationHandler.checkPermissionFrag(this)
        // [END gps_and_location_permission_handle]

        // [START place_client_init]
        //check google api key
        val apiKey = baseActivity!!.getGoogleApiKey()
        if (apiKey == "") {
            showToast(resId = R.string.api_key_required)
            return
        }

        // Setup Places Client
        if (!Places.isInitialized()) {
            context?.let {
                Places.initialize(it, apiKey)
            }
        }
        // [END place_client_init]

        // [START map_fragment_handling]
        val mapFragment = childFragmentManager.findFragmentById(R.id.map)!! as SupportMapFragment
        mapFragment.getMapAsync(this)
        // [END map_fragment_handling]

        //set-up place
        setupPlaceAutoComplete()

        //listen socket update
        mViewModel.listen()

        //add observers
        observers()

    }




    //endregion

    //region View Actor Method
    override fun onApiError(throwable: Throwable) {
        showToast(message = throwable.localizedMessage)
    }

    override fun addMarker(coordinates: LatLng, title: String) {
        mGoogleMap.addMarker(MarkerOptions().position(coordinates).title(title))

    }

    override fun updateMapCamera(coordinates: LatLng, duration: Int) {
        mGoogleMap.animateCamera(buildCameraUpdate(coordinates), duration, null)
    }

    override fun goToCurrentLocation() {
        mUserCurrentCoordinates?.let {
            updateMapCamera(it, CAMERA_UPDATE_DURATION)
        }
    }

    override fun showWithingRadiusDialog() {
        val alertDialog: AlertDialog? = baseActivity.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton(
                    R.string.dialog_btn_ok
                ) { _, _ ->

                }
            }
            builder.setMessage(getString(R.string.dialog_msg_radius, Constants.RADIUS_LIMIT))
                .setTitle(R.string.dialog_title_radius)

            builder.create()
        }
        alertDialog!!.show()
    }

    override fun onErrorThrowable(t: Throwable) {
        showToast(message = t.localizedMessage)

    }

    override fun doWithSocketData(result: JSON) {
        Log.d(HomeFragment::class.java.simpleName, result.toString())
    }

    //endregion

    override fun onMapReady(map: GoogleMap?) {
        mGoogleMap = map!!
        mGoogleMap.uiSettings.isCompassEnabled = true
        mGoogleMap.uiSettings.isMapToolbarEnabled = true
        mGoogleMap.uiSettings.isMyLocationButtonEnabled = false
        mGoogleMap.uiSettings.setAllGesturesEnabled(true)
        mViewModel.enableControllers.postValue(true)
        //stop loader
        mViewModel.getLoading().postValue(false)
    }

    override fun checkingPermission() {

    }

    override fun askingPermission() {

    }

    override fun permissionGranted() {
        //start location service if permission granted
        if (isServiceAlreadyRunning().not())
            startLocationService()
    }

    override fun permissionDenied() {

    }

    override fun checkingGPS() {

    }

    override fun turningOnGPS() {

    }

    override fun GPSTurnedOn() {
        mLocationHandler.startLocationUpdate()

    }


    override fun onError(message: String) {
        showToast(message = message)
    }


    /**
     *method to set-up place autocomplete fragment
     */
    private fun setupPlaceAutoComplete() {

        val autocompleteFragment =
            childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(baseActivity!!.getRequiredPlaceFields())
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                GPSHandler.STICK_TO_CURRENT_LOCATION = false
                mGoogleMap.clear()

                addMarker(place.latLng!!, place.name!!)
                updateMapCamera(place.latLng!!, CAMERA_UPDATE_DURATION)
                mViewModel.checkForRadius(mUserCurrentCoordinates!!, place.latLng!!)

            }

            override fun onError(status: Status) {
                status.statusMessage?.let {
                    showToast(message = it)
                }

            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GPS_ENABLE) {
                mLocationHandler.handlerResult()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationHandler.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onResume() {
        super.onResume()
        mLocationHandler.startLocationUpdate()
    }


    override fun onPause() {
        super.onPause()
        mLocationHandler.stopLocationUpdates()
    }

    private fun isServiceAlreadyRunning() = isTrackingRunning && isServiceRunning
    private fun startLocationService() = mServiceManager.startService()

    fun stopLocationService() {
        mServiceManager.stopService()
        (baseActivity as DashboardActivity).clearNotifications()
    }
    private fun observers() {
        mLocationHandler.locationUpdate.observe(viewLifecycleOwner, Observer { coordinates->
            mUserCurrentCoordinates = coordinates
        })
        mLocationHandler.currentLocationAvailable.observe(viewLifecycleOwner, Observer { coordinates->
            mUserCurrentCoordinates = coordinates
            mGoogleMap.isMyLocationEnabled = true
            updateMapCamera(coordinates, CAMERA_UPDATE_DURATION)
        })
    }
}