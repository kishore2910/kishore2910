package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.ShopInsightData
import com.powersoaps.distributorsales.databinding.ActivityShopInSightsBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import com.powersoaps.distributorsales.ui.utils.Session



class ShopInSightsActivity : BaseActivity() {

    private val shopInSightsBinding by lazy { ActivityShopInSightsBinding.inflate(layoutInflater) }

    private val homeViewModel: HomeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(shopInSightsBinding.root)
        homeViewModel.Shopinsightdata.observe(this, shopInsightResponse)
        if (isNetworkConnected(this))
        {
            loadingDialog(true)
            homeViewModel.ShopInSight(Session.userToken,HomeFragment.selectedShopId)

        }
        else startActivity(Intent(this, NoInternetActivity::class.java))

        shopInSightsBinding.shopName.text=HomeFragment.selectedShopName

        shopInSightsBinding.back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    @SuppressLint("SetTextI18n")
    private val shopInsightResponse = Observer<ShopInsightData> {
        loadingDialog(false)
        System.out.println(it)
        if (it.status_code == 200) {
            shopInSightsBinding.totalVolume.text=it.data!!.full_count_items
            shopInSightsBinding.productcount.text=it.data.monthly_items
        }
        else
        {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
}