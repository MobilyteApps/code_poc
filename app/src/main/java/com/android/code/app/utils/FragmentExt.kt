package com.android.code.app.utils

/**
 * Extension functions for Fragment.
 */

import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.code.app.base.GlobalApplication
import com.android.code.app.base.ViewModelFactory
/**
 * @AUTHOR Amandeep Singh
 */

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = GlobalApplication.graphQlApiHandler
    val prefManager = GlobalApplication.prefManager
    val socketHandler = GlobalApplication.socketHandler
    return ViewModelFactory(repository, prefManager,socketHandler)
}

fun Fragment.showToast(resId: Int? = null, message: String? = null) {
    println("toast message-->$message")
    activity?.let {
        Toast.makeText(
            it, if (resId != null) {
                it.getString(resId)
            } else {
                message!!
            }, Toast.LENGTH_SHORT
        ).show()

    }

}