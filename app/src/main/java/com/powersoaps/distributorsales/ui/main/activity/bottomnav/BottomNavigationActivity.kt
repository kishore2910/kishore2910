package com.powersoaps.distributorsales.ui.main.activity.bottomnav

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.databinding.ActivityBottomNavigationBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.AddNewShopActivity

class BottomNavigationActivity : BaseActivity() {

    private val bottomNavbinding by lazy { ActivityBottomNavigationBinding.inflate(layoutInflater) }


    private val navController by lazy { findNavController(R.id.fragment) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(bottomNavbinding.root)
        initBottomNavigationView()

        if (!areNotificationsEnabled(this)) {
            showNotificationPermissionDialog(this)
        }
    }

    private fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showNotificationPermissionDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("")
            .setMessage("Allow PowerSoaps Salesman App to send you notifications?")
            .setCancelable(false)
            .setPositiveButton("Allow") { dialog, _ ->
                dialog.dismiss()
                openAppNotificationSettings(context)
            }
            .setNegativeButton("Don't Allow") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun openAppNotificationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
        context.startActivity(intent)
    }

    private fun initBottomNavigationView() {
        bottomNavbinding.apply {
            bottomNavbinding.bottomNavigation.setupWithNavController(navController)
            bottomNavbinding.bottomNavigation.itemIconTintList = null

            bottomNavbinding.addButton.setOnClickListener {
                if (HomeFragment.unit_token=="null")
                    Toast.makeText(this@BottomNavigationActivity, "Schedule not allocated", Toast.LENGTH_SHORT).show()
                else startActivity(Intent(this@BottomNavigationActivity, AddNewShopActivity::class.java))
            }

            bottomNavbinding.bottomNavigation.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.homeMenu -> {
                        if (bottomNavbinding.bottomNavigation.selectedItemId != it.itemId) {
                            bottomNavbinding.addButton.visibility=View.VISIBLE
                            navController.navigate(R.id.homeFragment)
                            navController.popBackStack(it.itemId, inclusive = false)
                            return@setOnItemSelectedListener true
                        }
                    }
                    R.id.pendingMenu -> {
                        if (bottomNavbinding.bottomNavigation.selectedItemId != it.itemId) {
                            bottomNavbinding.addButton.visibility=View.INVISIBLE
                            navController.navigate(R.id.searchFragment)
                            navController.popBackStack(it.itemId, inclusive = false)
                            return@setOnItemSelectedListener true
                        }
                    }

                    R.id.settingMenu -> {
                        if (bottomNavbinding.bottomNavigation.selectedItemId != it.itemId) {
                            bottomNavbinding.addButton.visibility=View.INVISIBLE
                            navController.navigate(R.id.settingFragment)
                            navController.popBackStack(it.itemId, inclusive = false)
                            return@setOnItemSelectedListener true
                        }
                    }
                }
                return@setOnItemSelectedListener false
            }
        }
    }

    override fun onBackPressed() {
        bottomNavbinding.bottomNavigation.selectedItemId = R.id.homeMenu
    }

}