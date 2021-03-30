package com.android.code.app.base

import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.android.code.app.R
import com.android.code.app.utils.showToast
import com.google.android.libraries.places.api.model.Place


/**
 * @AUTHOR Amandeep Singh
 * */
abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel<out BaseViewActor>> :
    AppCompatActivity() {
    lateinit var viewDataBinding: T
    var mBaseViewModel: V? = null
    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    abstract val isMakeStatusBarTransparent: Boolean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        performDataBinding()
        if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
            val window = window
            if (isMakeStatusBarTransparent) {
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorTransparent)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                window.statusBarColor = ContextCompat.getColor(
                    this,
                    R.color.colorPrimaryDark
                )
            }
        }
        // observeBaseValues
        observeCommonData()
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        this.mBaseViewModel = if (mBaseViewModel == null) viewModel else mBaseViewModel
        viewDataBinding.setVariable(bindingVariable, mBaseViewModel)
        viewDataBinding.executePendingBindings()
    }


    private fun observeCommonData() {
        // observe general error
        viewModel.getMessage().observe(this, Observer {
            if (null != it) {
                showToast(message = it)
            }
        })
    }


    fun oneStepBack() {
        val fts = supportFragmentManager.beginTransaction()
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount >= 1) {
            fragmentManager.popBackStackImmediate()
            fts.commit()
        } else {
            supportFinishAfterTransition()
        }
    }


    fun getGoogleMapDirectionUri(lat: Double, lng: Double): String {
        return "http://maps.google.com/maps?daddr=${lat},${lng}"
    }

     fun clearNotifications() {
        val notificationManager =
            this@BaseActivity.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()

    }


    private fun clearUserData() {
        viewModel.getSharedPreference().clearPrefData()
    }

    fun handleUserExit() {
        clearNotifications()
        clearUserData()
    }


    fun getGoogleApiKey(): String {
        return getString(R.string.google_key)
    }

    fun getRequiredPlaceFields(): List<Place.Field> {
        return listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
    }


    fun executeNavigation(fragmentNavigationBuilder: FragmentNavigationBuilder) {
        val currentFragment: Fragment = fragmentNavigationBuilder.fragment
        val fts = supportFragmentManager.beginTransaction()
        currentFragment.arguments = fragmentNavigationBuilder.bundle
        if (fragmentNavigationBuilder.isAddFragment)
            fts.add(
                fragmentNavigationBuilder.container!!,
                currentFragment,
                currentFragment.javaClass.simpleName
            )
        else
            fts.replace(
                fragmentNavigationBuilder.container!!,
                currentFragment,
                currentFragment.javaClass.simpleName
            )

        if (fragmentNavigationBuilder.isBackStack)
            fts.addToBackStack(currentFragment.javaClass.simpleName)
        fts.commit()
    }
}

data class FragmentNavigationBuilder(
    var fragment: Fragment,
    var container: Int? = null,
    var isAddFragment: Boolean = false,
    var isBackStack: Boolean = false,
    var bundle: Bundle? = null
) {
    fun container(container: Int) = apply { this.container = container }
    fun isAddFragment(isAddFragment: Boolean) = apply { this.isAddFragment = isAddFragment }
    fun isBackStack(isBackStack: Boolean) = apply { this.isBackStack = isBackStack }
    fun bundle(bundle: Bundle?) = apply { this.bundle = bundle }
    fun build() = FragmentNavigationBuilder(fragment, container, isAddFragment, isBackStack, bundle)
}



