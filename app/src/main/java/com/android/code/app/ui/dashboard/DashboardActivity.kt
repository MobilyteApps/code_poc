package com.android.code.app.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import com.android.code.app.BR
import com.android.code.app.R
import com.android.code.app.base.BaseActivity
import com.android.code.app.base.FragmentNavigationBuilder
import com.android.code.app.databinding.ActivityDashbaordBinding
import com.android.code.app.ui.home.HomeFragment
import com.android.code.app.utils.Constants
import com.android.code.app.utils.getViewModelFactory
import com.android.code.app.utils.showToast


/**
 * @AUTHOR Amandeep Singh
 */
class DashboardActivity : BaseActivity<ActivityDashbaordBinding, DashboardViewModel>(),
        DashboardActivityViewActor {

    //region Variables
    private var mBinding: ActivityDashbaordBinding? = null
    private val mViewModel by viewModels<DashboardViewModel> { getViewModelFactory() }
    private var mExitCounter = 1
    //endregion

    //region Base Class Methods
    override val viewModel: DashboardViewModel
        get() = mViewModel

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_dashbaord

    override val isMakeStatusBarTransparent: Boolean
        get() = false
    //endregion

    //region Life Cycle Methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //set binding
        mBinding = viewDataBinding
        // set view actor
        mViewModel.setViewActor(this)
        //Load First Fragment
        loadHomeFragment()
    }
    //endregion


    //region View Actor Method
    override fun loadHomeFragment() {
        val screen = FragmentNavigationBuilder(HomeFragment())
                .container(R.id.fl_dashboard_container)
                .isAddFragment(false)
                .isBackStack(false)
                .bundle(null)
                .build()
        executeNavigation(screen)
    }
//endregion


    override fun onBackPressed() {
        //Manage app exit
        if (mExitCounter == 0) {
            super.onBackPressed()
        } else {
            showToast(resId = R.string.press_once_more)
            mExitCounter--
        }
        //reset exit counter
        Handler().postDelayed({ mExitCounter = 1 }, Constants.EXIT_COUNTER_RESET_DELAY)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        supportFragmentManager.findFragmentById(R.id.fl_dashboard_container)!!.onActivityResult(requestCode, resultCode, data)
    }
}