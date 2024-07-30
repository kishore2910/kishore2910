package com.powersoaps.distributorsales.ui.main.activity.internet

import android.os.Bundle
import android.widget.Toast
import com.powersoaps.distributorsales.databinding.ActivityNoInternetBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener

class NoInternetActivity : BaseActivity() {

    private val noInternetBinding by lazy { ActivityNoInternetBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(noInternetBinding.root)
        noInternetBinding.Refresh.setOnDebounceListener {
            internet()
        }
    }
    private fun internet() {
        if (isNetworkConnected(applicationContext)) {
            finish()
        } else {
            Toast.makeText(this, "Please check your connection", Toast.LENGTH_SHORT).show()
        }
    }
}