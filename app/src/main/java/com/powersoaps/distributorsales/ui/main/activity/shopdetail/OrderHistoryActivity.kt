package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.powersoaps.distributorsales.data.model.EditOrderData
import com.powersoaps.distributorsales.data.model.LocationData
import com.powersoaps.distributorsales.data.model.OrderHistoryData
import com.powersoaps.distributorsales.data.model.ServerResponse
import com.powersoaps.distributorsales.databinding.ActivityOrderHistoryBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.PendingFragment.Companion.pendingDetails
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.adapter.OrderHistoryAdapter
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel

class OrderHistoryActivity : BaseActivity() {


    private val orderHistoryAdapter by lazy {OrderHistoryAdapter(this,orderhistorydata,::onClicked,::onHolderClicked,::onTokenPass)}

    private val homeViewModel: HomeViewModel by viewModels()

    private val authViewModel: AuthViewModel by viewModels()

    private val historyBinding by lazy { ActivityOrderHistoryBinding.inflate(layoutInflater) }

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    var tokenSelect = ""
    var orderToken = ""

    var discountEnable = false

    companion object {
        private lateinit var orderhistorydata: ArrayList<OrderHistoryData.OrderHistoryObjects.OrderHistoryList>
        lateinit var editPreviousData: ArrayList<EditOrderData.PreviousOrderList.OrderProductDetails>
        lateinit var orderId: String
        var empLatitude: Double = 0.0
        var empLongitude: Double = 0.0
        var pendingText : String = "Pending"

    }

    var shopToken = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(historyBinding.root)
        historyBinding.ordername.text = HomeFragment.selectedShopName
        homeViewModel.OrderHistoryData.observe(this@OrderHistoryActivity, OrderHistoryResponse)
        homeViewModel.editOrderData.observe(this@OrderHistoryActivity, editResponseData)
        homeViewModel.orderdeliverdata.observe(this@OrderHistoryActivity, OrderdeliveryResponse)
        authViewModel.LocationData.observe(this@OrderHistoryActivity, productLocationResponse)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (isNetworkConnected(this)) {
            loadingDialog(true)
            //api call sending order histroy
            homeViewModel.OrderHistory(Session.userToken, HomeFragment.selectedShopId)
        } else startActivity(Intent(this, NoInternetActivity::class.java))
        orderlist()

        setContentView(historyBinding.root)

        historyBinding.back.setOnDebounceListener {
            onBackPressed()
        }
    }

    private fun onClicked(beatData: OrderHistoryData.OrderHistoryObjects.OrderHistoryList) {

        if (beatData.status == "Pending") {
            tokenSelect = beatData.shop_token.toString()
            orderToken = beatData.order_token.toString()
            Log.d("TAG", "onClicked: ${beatData.shop_token}")
            showAlert("Are you sure do you want to deliver")

        } else {
            startActivity(
                Intent(this, OrderDetailsActivity::class.java)
                    .putExtra("shoptoken", beatData.shop_token)
                    .putExtra("orderid", beatData.order_token)
            )
        }
    }

    private fun onHolderClicked(beatData: OrderHistoryData.OrderHistoryObjects.OrderHistoryList) {
            startActivity(
                Intent(this, OrderDetailsActivity::class.java)
                    .putExtra("shoptoken", beatData.shop_token)
                    .putExtra("orderid", beatData.order_token)
            )
    }

    private fun onTokenPass(task: OrderHistoryData.OrderHistoryObjects.OrderHistoryList) {

        orderId = task.order_token.toString()

        if (isNetworkConnected(this)) {
            loadingDialog(true)
            homeViewModel.editOrder(task.order_token.toString())
        } else startActivity(Intent(this, NoInternetActivity::class.java))
    }

    private fun orderlist() {
        orderhistorydata = ArrayList()
    }

    //getting order history data
    @SuppressLint("SetTextI18n")
    private val OrderHistoryResponse = Observer<OrderHistoryData> {
        loadingDialog(false)
        if (it.status_code == 200) {
            println(it)
            for (a in 0 until it.data!!.order_hisyory_data?.size!!) {
                orderhistorydata.add(
                    OrderHistoryData.OrderHistoryObjects.OrderHistoryList(
                        it.data.order_hisyory_data?.get(a)?.schedule_date,
                        it.data.order_hisyory_data?.get(a)?.order_token,
                        it.data.order_hisyory_data?.get(a)?.shop_token,
                        it.data.order_hisyory_data?.get(a)?.shop_name,
                        it.data.order_hisyory_data?.get(a)?.status,
                        it.data.order_hisyory_data?.get(a)?.billing_amount,
                        it.data.order_hisyory_data?.get(a)?.paid_amount,
                        it.data.order_hisyory_data?.get(a)?.outstanding_amt,
                        it.data.order_hisyory_data?.get(a)?.items
                    )
                )
            }
            Log.d("TAG", "OrderHistoryResponse: $orderhistorydata ")

            if (orderhistorydata.size == 0) {
                historyBinding.empty.visibility = View.VISIBLE
                historyBinding.historyItem.visibility = View.GONE
            } else {
                historyBinding.empty.visibility = View.GONE
                historyBinding.historyItem.visibility = View.VISIBLE
            }
            historyBinding.historyItem.adapter = orderHistoryAdapter
        } else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val editResponseData = Observer<EditOrderData> {
        editPreviousData = ArrayList()
        loadingDialog(false)
        if (it.status_code == 200) {
            Log.d("TAG", "editResponseData: ${it.data}")
            for (i in 0 until it.data?.order_details!!.size) {
                editPreviousData.add(
                    EditOrderData.PreviousOrderList.OrderProductDetails(
                        it.data.order_details[i].product_token,
                        it.data.order_details[i].product_name,
                        it.data.order_details[i].quantity,
                        it.data.order_details[i].units,
                        it.data.order_details[i].amount,
                        it.data.order_details[i].piece_count,
                        it.data.order_details[i].discountEnable,
                        it.data.order_details[i].discount,
                        it.data.order_details[i].totalValue,

                    )
                )
                Log.d("TAG", "editPreviousData: $editPreviousData")
            }
            startActivity(
                Intent(this, TakeOrderActivity::class.java)
                    .putExtra("type", "edit")
                    .putExtra("order_id", orderId)
            )
        } else {
            Log.d("TAG", "editResponseData: ${it.message}")
            Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val mLocationRequest = LocationRequest()
                    mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    mLocationRequest.interval = 6000000
                    mLocationRequest.fastestInterval = 6000000
                    mLocationRequest.numUpdates = 1000

                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    mFusedLocationClient.requestLocationUpdates(
                        mLocationRequest, mLocationCallback,
                        Looper.myLooper()!!
                    )
                }
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setMessage("Your GPS seems to be disabled, please enable it?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, id ->
                        dialog.dismiss()
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                    }
                    .setNegativeButton(
                        "No"
                    ) { dialog, id ->
//                        initTab()
                        dialog.cancel()
                    }
                val alert = builder.create()
                alert.show()
            }
        } else {
            requestPermissions()
        }

    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location = locationResult.lastLocation
            empLatitude = mLastLocation?.latitude!!
            empLongitude = mLastLocation.longitude
            if (Session.userToken.isEmpty()) {
                Toast.makeText(
                    this@OrderHistoryActivity,
                    "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (empLatitude == 0.0 && empLongitude == 0.0) {
                Toast.makeText(
                    this@OrderHistoryActivity,
                    "Please Enable Location",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
//                authViewModel.locationList(Session.userToken,
//                    OrderDetailsActivity.empLatitude,
//                    OrderDetailsActivity.empLongitude,shoptoken
//                )
                // lat lng

//              send location to db
                authViewModel.locationList(
                    Session.userToken,
                    empLatitude,
                    empLongitude,
                    HomeFragment.selectedShopId
                )

            }
        }
    }

//getting location response
    private val productLocationResponse = Observer<LocationData> {
        if (it.status_code == 200) {
//            placeOrder()
            OrderCompleteDelivery()
//            Toast.makeText(this, "Delivered Succussfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun OrderCompleteDelivery() {
        if (isNetworkConnected(this)) {
            loadingDialog(true)
            //sending order delivery to db
            homeViewModel.OrderDeliver(Session.userToken, tokenSelect, orderToken,pendingText)
        } else startActivity(Intent(this, NoInternetActivity::class.java))
    }

    //getting order delivery response

    private val OrderdeliveryResponse = Observer<ServerResponse> {
        loadingDialog(false)
        if (it.status_code == 200) {
//            finish()
            startActivity(Intent(this, BottomNavigationActivity::class.java))
//            SuccessAlert("Your Order Successfully Delivered ")
        } else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showAlert(message: String) {
        val builder = AlertDialog.Builder(this@OrderHistoryActivity)
        builder.setTitle("")
            .setMessage(message)
            .setPositiveButton("Yes") { dialog, _ ->
                pendingDetails.clear()
                Log.d("TAG", "showAlert: $pendingDetails")

                // Handle the positive button click (if needed)
                getLastLocation()

                dialog.dismiss()

            }
            .setNegativeButton("No") { dialog, _ ->
                // Handle the negative button click (if needed)
                historyBinding.historyItem.adapter?.notifyDataSetChanged()
                dialog.dismiss()

            }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun SuccessAlert(message: String) {
        val builder = AlertDialog.Builder(this@OrderHistoryActivity)
        builder.setTitle("")
            .setMessage(message)
            .setPositiveButton("Ok") { dialog, _ ->
                // Handle the positive button click (if needed)

                dialog.dismiss()


            }
            .setNegativeButton("") { dialog, _ ->
                // Handle the negative button click (if needed)
                dialog.dismiss()

            }
        val alertDialog = builder.create()
        alertDialog.show()
    }
}