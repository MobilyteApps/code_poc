package com.android.code.app.base


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.code.app.data.local.pref.PrefManager
import com.android.code.app.data.remote.graphql.GraphQlApiHandler
import com.android.code.app.data.remote.socket.SocketHandler
import com.android.code.app.ui.dashboard.DashboardViewModel
import com.android.code.app.ui.home.HomeViewModel
import com.android.code.app.ui.splash.SplashViewModel

/**
 * @AUTHOR Amandeep Singh
 * */

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val graphQlApiHandler: GraphQlApiHandler, private val prefManager: PrefManager,private val socketHandler: SocketHandler) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>) =
            with(modelClass) {
                when {
                    isAssignableFrom(SplashViewModel::class.java) ->
                        SplashViewModel(prefManager)
                    isAssignableFrom(DashboardViewModel::class.java) ->
                        DashboardViewModel(prefManager)
                    isAssignableFrom(HomeViewModel::class.java) ->
                        HomeViewModel(graphQlApiHandler,prefManager,socketHandler)

                    else ->
                        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            } as T
}