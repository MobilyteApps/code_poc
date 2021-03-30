package com.android.code.app.utils

/**
 * @AUTHOR Amandeep Singh
 * Extension functions for activity.
 * */
import android.app.Activity
import android.widget.Toast
import com.android.code.app.base.GlobalApplication
import com.android.code.app.base.ViewModelFactory

fun Activity.getViewModelFactory(): ViewModelFactory {
    val repository = GlobalApplication.graphQlApiHandler
    val prefManager = GlobalApplication.prefManager
    val socketHandler = GlobalApplication.socketHandler
    return ViewModelFactory(repository, prefManager,socketHandler)
}


fun Activity.showToast(resId: Int? = null, message: String? = null) {
    Toast.makeText(
        this, if (resId != null) {
            this.getString(resId)
        } else {
            message!!
        }, Toast.LENGTH_SHORT
    ).show()

}
