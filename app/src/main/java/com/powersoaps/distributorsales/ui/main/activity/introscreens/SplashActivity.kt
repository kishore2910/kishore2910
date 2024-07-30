package com.powersoaps.distributorsales.ui.main.activity.introscreens

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.data.model.VersionCheckData
import com.powersoaps.distributorsales.data.pref.Preference
import com.powersoaps.distributorsales.databinding.ActivitySplashBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.activity.update.UpdateActivity
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.Util
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel


@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {

    private val splashScreen by lazy { ActivitySplashBinding.inflate(layoutInflater) }

    private val authViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(splashScreen.root)

    }

    override fun onResume() {
        super.onResume()
        authViewModel.versionCheckResponse.observe(this@SplashActivity, versionResponse)
        Handler(Looper.getMainLooper()).postDelayed(
            {
                if (isNetworkConnected(this)) {
                    loadingDialog(true)
                    val pInfo: PackageInfo =
                        this.packageManager.getPackageInfo(this.packageName, 0)
                    val version = pInfo.versionName
                    authViewModel.VersionCheck(version)
                } else {
                    startActivity(Intent(this, NoInternetActivity::class.java))
                }

            }, 3000
        )
    }

    private fun data() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private val versionResponse = Observer<VersionCheckData> {
        loadingDialog(false)
        if (it.status_code == 200) {
            if (it.isForceUpdate == true) {
                Util.APP_URL = it.app_link.toString()
                startActivity(Intent(this, UpdateActivity::class.java))
                finish()
            } else {
                if (Preference(context = this).isLoggedIn()) {
                    val parentData = Preference(this).getParentDetails()
                    Session.userID = parentData["id"].toString()
                    Session.userToken = parentData["token"].toString()
                    Session.userName = parentData["name"].toString()
                    Session.userCode = parentData["employees_code"].toString()
                    Session.userCode = parentData["deparment_token"].toString()
                    Session.userGender = parentData["gender"].toString()
                    Session.userMobile = parentData["mobile_number"].toString()
                    Session.userRegion = parentData["region"].toString()
                    Session.userDepName = parentData["dep_name"].toString()
                    launchActivity(BottomNavigationActivity::class.java, isClearPreviousTask = true)
                } else {
                    data()
                }
            }
        } else {
            Toast.makeText(this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show()
        }
    }
}