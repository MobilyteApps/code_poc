package com.android.code.app.services.location

import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * @AUTHOR Amandeep Singh
 */
class ServiceManager(
    private val context: Context,
    private val serviceIntent: Intent
) : ServiceCallback {
    override fun startService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    override fun stopService() {
        context.stopService(serviceIntent)
    }
}