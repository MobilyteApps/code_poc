package com.android.code.app.ui.home

import androidx.lifecycle.MutableLiveData
import com.android.code.app.base.BaseViewModel
import com.android.code.app.data.local.pref.PrefManager
import com.android.code.app.data.model.custom.SocketThrowable
import com.android.code.app.data.remote.graphql.GraphQlApiHandler
import com.android.code.app.data.remote.socket.SocketHandler
import com.android.code.app.utils.Constants
import com.google.android.gms.maps.model.LatLng
import eu.amirs.JSON
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers


/**
 * @AUTHOR Amandeep Singh
 * @date 13/11/2019
 */
class HomeViewModel(
    val graphQlApiHandler: GraphQlApiHandler,
    prefManager: PrefManager,
    private val socketHandler: SocketHandler
) : BaseViewModel<HomeViewActor>(prefManager) {
    //region Variables
    val error = MutableLiveData<Boolean>()
    val errorMessage = MutableLiveData<String>()
    val enableControllers = MutableLiveData<Boolean>()
    //endregion

    //region Init Method
    init {
        error.postValue(false)
        enableControllers.postValue(false)
    }
    //endregion

    fun onClickCurrentLocation() {
        getViewActor().goToCurrentLocation()
    }

    fun checkForRadius(userCurrentCoordinates: LatLng?, destination: LatLng) {
        userCurrentCoordinates?.let {
            if (checkWithingRadius(Constants.RADIUS_LIMIT, it, destination)) {
                getViewActor().showWithingRadiusDialog()
            }
        }

    }

    /**
     *Listen required socket event
     */
    fun listen() {
        addDisposable(
            socketHandler.getLocation
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableObserver<JSON>() {
                    override fun onComplete() {


                    }

                    override fun onNext(result: JSON) {
                        getViewActor().doWithSocketData(result)
                    }

                    override fun onError(e: Throwable) {
                        getViewActor().onErrorThrowable(SocketThrowable(e.localizedMessage))
                    }
                })
        )
    }


}