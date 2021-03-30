package com.android.code.app.data.remote.graphql

/**
 * @AUTHOR Amandeep Singh
 */
open interface BaseResponseListener {
    fun onError(message: String?)
    fun onAuthInfoField(message: String?)
    fun onResponseMessageField(message: String?)
}