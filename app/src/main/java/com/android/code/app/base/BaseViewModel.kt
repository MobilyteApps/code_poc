package com.android.code.app.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.code.app.data.local.pref.PrefManager
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * @AUTHOR Amandeep Singh
 * */
abstract class BaseViewModel<N : BaseViewActor>(
    private val prefManager: PrefManager
) : ViewModel() {

    //region variables
    private var mMessage: MutableLiveData<String>? = null
    private var mLoading: MutableLiveData<Boolean>? = null

    private val mCompositeDisposable = CompositeDisposable()
    private lateinit var mViewActor: WeakReference<N>
    val alertDeleteHandler = MutableLiveData<String>()
    //endregion


    fun getMessage(): MutableLiveData<String> {
        if (mMessage == null) mMessage = MutableLiveData()
        return mMessage!!
    }

    fun getLoading(): MutableLiveData<Boolean> {
        if (mLoading == null) mLoading = MutableLiveData()
        return mLoading!!
    }


    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    override fun onCleared() {
        mCompositeDisposable.dispose()
        super.onCleared()

    }

    fun getSharedPreference(): PrefManager {
        return prefManager
    }

    fun getViewActor(): N {
        return mViewActor.get().let { it as N }
    }

    fun setViewActor(viewActor: N) {
        this.mViewActor = WeakReference(viewActor)
    }

}