package com.android.code.app.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.android.code.app.BR
import com.android.code.app.R
import com.android.code.app.base.BaseActivity
import com.android.code.app.base.BaseViewActor
import com.android.code.app.base.ViewModelFactory
import com.android.code.app.databinding.ActivitySplashBinding
import com.android.code.app.ui.dashboard.DashboardActivity
import com.android.code.app.ui.home.HomeViewModel
import com.android.code.app.utils.getViewModelFactory


/**
 * @AUTHOR Amandeep Singh
 * */
class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(), BaseViewActor {

    //region Variables
    private var mBinding: ActivitySplashBinding? = null
    private var mHandler: Handler? = null
    private val mViewModel by viewModels<SplashViewModel> { getViewModelFactory() }
    //endregion


    //region Base Class Methods
    override val viewModel: SplashViewModel
        get() = mViewModel

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_splash

    override val isMakeStatusBarTransparent: Boolean
        get() = true
    //endregion


    //region Life Cycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set binding
        mBinding = viewDataBinding
        // set view actor
        mViewModel!!.setViewActor(this)
        mHandler = Handler()

    }

    override fun onPause() {
        super.onPause()
        if (mHandler != null)
            mHandler!!.removeCallbacks(splashRunnable)//removing callback
    }

    override fun onResume() {
        super.onResume()
        if (mHandler != null) {
            mHandler!!.postDelayed(splashRunnable, 1000)//adding callback
        }
    }
    //endregion

    private val splashRunnable = {
         //check user session is available
         if (mViewModel!!.getSharedPreference().accessToken.isEmpty()) {
             //USER SESSION NOT AVAILABLE

             //navigate user to login screen
             startActivity(Intent(this@SplashActivity, DashboardActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
             finish()
         } else {
             //USER SESSION IS AVAILABLE

             //navigate user to dashboard screen
             startActivity(Intent(this@SplashActivity, DashboardActivity::class.java))
             finish()
         }
    }

}