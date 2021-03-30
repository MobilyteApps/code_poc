package com.android.code.app.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.android.code.app.utils.showToast


/**
 * @AUTHOR Amandeep Singh
 * */
abstract class BaseFragment<T : ViewDataBinding, V : BaseViewModel<out BaseViewActor>> :
    Fragment() {

    var baseActivity: BaseActivity<T, V>? = null
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


    override fun onAttach(context: Context) {
        super.onAttach(context!!)
        if (context is BaseActivity<*, *>) {
            val activity = context as BaseActivity<T, V>?
            this.baseActivity = activity
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBaseViewModel = viewModel
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewDataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewDataBinding.setVariable(bindingVariable, mBaseViewModel)
        // Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return viewDataBinding.root
    }

    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewDataBinding.executePendingBindings()
        observeCommonData()
    }


    private fun observeCommonData() {
        // observe general message
        viewModel.getMessage().observe(this, Observer {
            showToast(message = it)
        })


    }

}

