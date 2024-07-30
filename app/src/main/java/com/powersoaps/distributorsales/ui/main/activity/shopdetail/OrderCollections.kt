package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.data.model.OrderHistoryData
import com.powersoaps.distributorsales.databinding.ActivityOrderCollectionsBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.adapter.OrderHistoryAdapter
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList

class OrderCollections : BaseActivity() {

    private val ordercollectionsBinding by lazy { ActivityOrderCollectionsBinding.inflate(layoutInflater) }

    var shopname: String = ""

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var orderhistorydata: ArrayList<OrderHistoryData.OrderHistoryObjects.OrderHistoryList>
    private val orderHistoryAdapter by lazy { OrderHistoryAdapter(this, orderhistorydata,:: onClicked, ::onHolderClicked, :: onTokenPass) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ordercollectionsBinding.root)
        homeViewModel.OrderHistoryData.observe(this@OrderCollections, OrderHistoryResponse)
        val bundle = intent.extras
        if (bundle != null) {
            shopname = bundle.getString("shopname").toString()
        }
        ordercollectionsBinding.shopnamecollection.text = shopname
        if (isNetworkConnected(this))
        {
            loadingDialog(true)
            homeViewModel.OrderHistory(Session.userToken, HomeFragment.selectedShopId)
        }
        else startActivity(Intent(this, NoInternetActivity::class.java))
        orderlist()
        ordercollectionsBinding.back.setOnDebounceListener {
            onBackPressed()
        }
    }


    private fun orderlist() {
        orderhistorydata = ArrayList()
    }


    @SuppressLint("SetTextI18n")
    private val OrderHistoryResponse = Observer<OrderHistoryData> {
        loadingDialog(false)
        if (it.status_code == 200) {
            System.out.println(it)
            for (a in 0 until it.data!!.order_hisyory_data?.size!!)
            {
                orderhistorydata.add(
                    OrderHistoryData.OrderHistoryObjects.OrderHistoryList(
                        it.data!!.order_hisyory_data?.get(a)?.schedule_date,it.data.order_hisyory_data?.get(a)?.order_token,
                        it.data!!.order_hisyory_data?.get(a)?.shop_token,it.data.order_hisyory_data?.get(a)?.shop_name,
                        it.data.order_hisyory_data?.get(a)?.status,
                        it.data!!.order_hisyory_data?.get(a)?.billing_amount,it.data!!.order_hisyory_data?.get(a)?.paid_amount,
                        it.data!!.order_hisyory_data?.get(a)?.outstanding_amt, it.data!!.order_hisyory_data?.get(a)?.items
                    ) )
            }
            if (orderhistorydata.size==0)
            {
                ordercollectionsBinding.empty.visibility = View.VISIBLE
                ordercollectionsBinding.collectionheading.visibility = View.GONE
                ordercollectionsBinding.collectionsitems.visibility = View.GONE
                ordercollectionsBinding.collectioncount.setText("₹"+" "+"0")
            }
            else
            {
                ordercollectionsBinding.empty.visibility = View.GONE
                ordercollectionsBinding.collectionheading.visibility = View.VISIBLE
                ordercollectionsBinding.collectionsitems.visibility = View.VISIBLE
                val fmt = DecimalFormat("#,##,###")
                val billamount: Double = it.data.outstanding_value?.outstanding_amount_value!!.toDouble() //If your num is in String
                val currency = fmt.format(billamount)
                ordercollectionsBinding.collectioncount.setText("₹"+" "+currency)
            }
            ordercollectionsBinding.collectionsitems.adapter = orderHistoryAdapter
        }
        else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun onClicked(beatData: OrderHistoryData.OrderHistoryObjects.OrderHistoryList){
        startActivity(Intent(this, OrderDetailsActivity::class.java).putExtra("shoptoken",beatData.shop_token).putExtra("orderid",beatData.order_token))
    }

    private fun onHolderClicked(beatData: OrderHistoryData.OrderHistoryObjects.OrderHistoryList) {

        startActivity(
            Intent(this, OrderDetailsActivity::class.java)
                .putExtra("shoptoken", beatData.shop_token)
                .putExtra("orderid", beatData.order_token)
        )

    }
    private fun onTokenPass(task:OrderHistoryData.OrderHistoryObjects.OrderHistoryList){

    }
}