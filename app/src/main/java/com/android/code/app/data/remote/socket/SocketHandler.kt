package com.android.code.app.data.remote.socket

import android.util.Log
import com.android.code.app.utils.Constants
import com.google.android.gms.maps.model.LatLng
import eu.amirs.JSON
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONException
import org.json.JSONObject
import java.net.URISyntaxException

/**
 * @AUTHOR Amandeep Singh
 */
class SocketHandler {

    //region Variables
    private var mSocket: Socket? = null
    var getLocation: Observable<JSON>
    private val getLocationSubject = PublishSubject.create<JSON>()
    //endregion

    //region Companion Object
    companion object {
        val instance = SocketHandler()
    }
    //endregion

    //region Init Block
    init {
        try {
            mSocket = IO.socket(Constants.SOCKET_URL)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        getLocation = getLocationSubject

        connect()
    }
    //endregion

    private fun connect() {
        mSocket!!.on(Socket.EVENT_CONNECT) {
            Log.d(SocketHandler::class.java.simpleName,"socket-connected")
        }
        mSocket!!.on(Socket.EVENT_DISCONNECT) {
            Log.d(SocketHandler::class.java.simpleName,"socket-disconnected")
        }
        mSocket!!.on(Socket.EVENT_CONNECT_ERROR) {
            Log.d(SocketHandler::class.java.simpleName,"socket-connection-error")
        }
        mSocket!!.connect()
    }

    fun attach() {
        mSocket!!.on("updateLocation") { data ->
            if (data[0] is JSONObject) {
                val onResult = JSON(data[0].toString())
                getLocationSubject.onNext(onResult)
            }
        }
    }


    fun sendLocation(coordinates: LatLng) {
        val locationData = JSONObject()
        try {
            locationData.put("lat", coordinates.latitude)
            locationData.put("lang", coordinates.longitude)
            mSocket!!.emit("trackUserLocation", locationData, Ack { response ->
                if (response != null && response[0] is JSONObject) {
                    // handle server response
                }
            })
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }


}