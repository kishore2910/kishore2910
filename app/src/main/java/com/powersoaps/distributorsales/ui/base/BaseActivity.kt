package com.powersoaps.distributorsales.ui.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.FilterData
import com.powersoaps.distributorsales.data.model.LocationData
import com.powersoaps.distributorsales.data.model.ProductData
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.ShopDetailActivity
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.TakeOrderActivity
import com.powersoaps.distributorsales.ui.utils.DialogUtils
import com.powersoaps.distributorsales.ui.utils.NetworkHelper
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel

@SuppressLint("InflateParams")
open class BaseActivity : AppCompatActivity() {

    private var loadingDialog: AlertDialog? = null
    private lateinit var networkHelper: NetworkHelper
    var dialog: AlertDialog? = null
    var isRetry = MutableLiveData<Boolean>()


//    val PERMISSION_ID = 42
//    lateinit var mFusedLocationClient: FusedLocationProviderClient
//
//    companion object
//    {
//        var empLatitude:Double=0.0
//        var empLongitude:Double=0.0
//    }
//    private val authViewModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        authViewModel.LocationData.observe(this@BaseActivity, productResponse)
    }

    fun launchActivity(
        javaClass: Class<out AppCompatActivity>,
        bundle: Bundle? = null,
        isClearPreviousTask: Boolean = false
    ) {
        Intent(this, javaClass).apply {
            if (bundle != null)
                putExtras(bundle)

            if (isClearPreviousTask)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(this)

        }
    }


    fun errors(title: String, message: String) {
            val alert = AlertDialog.Builder(this).setMessage(message).setCancelable(false)
                .setPositiveButton("Proceed", DialogInterface.OnClickListener {
                        dialog, id -> dialog.dismiss()
                }.apply {
                    titleColor = ContextCompat.getColor(applicationContext, R.color.black)
                }).create()
            alert.setTitle(title)
            alert.show()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun statusBarCustom() {
        if (Build.VERSION.SDK_INT in 19..21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    private fun setWindowFlag(bits: Int, on: Boolean) {
        val win = window
        val winParams = win.attributes
        if (on) {
            winParams.flags = winParams.flags or bits
        } else {
            winParams.flags = winParams.flags and bits.inv()
        }
        win.attributes = winParams
    }

    fun loadingDialog(status: Boolean) {
        if (status) {
            loadingDialog?.dismiss()
            loadingDialog = DialogUtils.loadingDialog(this)
            loadingDialog?.show()
        } else {
            loadingDialog?.dismiss()
        }
    }

    fun isNetworkConnected(context: Context) : Boolean {
        networkHelper = NetworkHelper(context)
        return networkHelper.isNetworkConnected()
    }

    fun AppCompatActivity.checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                100
            )
        }
    }


    open val loader = Observer<Boolean> {
        if (it) {
            val view = layoutInflater.inflate(R.layout.loader, null)
//            val customLayout = layoutInflater.inflate(R.layout.loader, null)
            dialog = AlertDialog.Builder(this).setView(view).setCancelable(false).show()
            dialog!!.window!!.setLayout(260, ViewGroup.LayoutParams.WRAP_CONTENT)
//            Glide.with(this).load(R.raw.load).into(view.findViewById(R.id.ProgressBar))

        } else {
            dialog!!.dismiss()
        }
    }

}