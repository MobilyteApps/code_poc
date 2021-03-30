package com.android.code.app.base


/**
 * @AUTHOR Amandeep Singh
 * */
interface CommonViewActor : BaseViewActor {
    /**
     *Method to show API Error/Internet Error on view
     * @param throwable containing error
     */
    fun onApiError(throwable: Throwable)

}