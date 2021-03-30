package com.android.code.app.base

import android.app.Application
import com.android.code.app.data.local.pref.PrefManager
import com.android.code.app.data.remote.graphql.GraphQlApiHandler
import com.android.code.app.data.remote.socket.SocketHandler
import com.android.code.app.utils.InternetUtil

/**
 * @AUTHOR Amandeep Singh
 * */
class GlobalApplication : Application() {

    //region Companion Object
    companion object {
        private var app: GlobalApplication? = null
        val graphQlApiHandler: GraphQlApiHandler get() = GraphQlApiHandler.instance
        val socketHandler: SocketHandler get() = SocketHandler.instance
        val prefManager: PrefManager get() = PrefManager.getInstance().initPref(app!!.applicationContext)
    }
    //endregion

    override fun onCreate() {
        super.onCreate()
        app = this
        //init internet utils
        InternetUtil.init(this)
        //connect socket
        socketHandler.attach()
    }

}