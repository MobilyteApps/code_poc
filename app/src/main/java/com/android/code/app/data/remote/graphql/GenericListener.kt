package com.android.code.app.data.remote.graphql

/**
 * @AUTHOR Amandeep Singh
 */

open interface GenericListener<T> :
    BaseResponseListener {
    fun onResult(response: T)
}
