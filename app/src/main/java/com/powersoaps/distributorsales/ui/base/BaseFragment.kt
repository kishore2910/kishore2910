package com.powersoaps.distributorsales.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.powersoaps.distributorsales.ui.utils.DialogUtils
import com.powersoaps.distributorsales.ui.utils.NetworkHelper

open class BaseFragment: Fragment() {

    private var loadingDialog: AlertDialog? = null
    private lateinit var networkHelper: NetworkHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun loadingDialog(status: Boolean) {
        if (status) {
            loadingDialog?.dismiss()
            loadingDialog = activity?.let { DialogUtils.loadingDialog(it) }
            loadingDialog?.show()
        } else {
            loadingDialog?.dismiss()
        }
    }
    fun isNetworkConnected(context: Context) : Boolean {
        networkHelper = NetworkHelper(context)
        return networkHelper.isNetworkConnected()
    }
}