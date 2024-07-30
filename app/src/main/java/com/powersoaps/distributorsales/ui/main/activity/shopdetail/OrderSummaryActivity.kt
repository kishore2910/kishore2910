package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.*
import com.powersoaps.distributorsales.databinding.ActivityOrderSummaryBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment.Companion.isPlaceOrder
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.adapter.SelectedProductAdapter
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.text.DecimalFormat
import kotlin.math.roundToInt

class OrderSummaryActivity : BaseActivity() {

    private val activityOrderSummaryBinding by lazy { ActivityOrderSummaryBinding.inflate(layoutInflater) }

    private lateinit var loadingdata: ArrayList<ProductData.ProductListData>

    private val homeViewModel: HomeViewModel by viewModels()

    private val filtered = TakeOrderActivity.adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" } as ArrayList<ProductData.ProductListData>

    private val selectedProductAdapter by lazy { SelectedProductAdapter(finalSummaryCartList,::onClicked, ::totalAmount) }

    private val placeOrderDialog by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.dialog_order_complete, null))
        setCancelable(false)} }

    lateinit var Summarycartdata: ArrayList<ProductData.ProductListData>

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val authViewModel: AuthViewModel by viewModels()
    var type: String = ""
    var orderId : String = ""

    companion object
    {
        val summaryCartList : ArrayList<ProductData.ProductListData>  = ArrayList()
        val finalSummaryCartList : ArrayList<ProductData.ProductListData>  = ArrayList()
        var empLatitude:Double=0.0
        var empLongitude:Double=0.0

    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(activityOrderSummaryBinding.root)
        homeViewModel.orderPlaceData.observe(this, orderPlaceResponse)
        homeViewModel.orderUpdateData.observe(this, orderPlaceResponse)
        authViewModel.LocationData.observe(this, productResponse)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Summarycartdata= ArrayList()

        Log.d("TAG", "onCreate: filtered $filtered")

        val bundle = intent.extras
        if (bundle != null) {
            type = bundle.getString("type").toString()
            orderId = bundle.getString("order_id").toString()
        }
//        calculateTotalPrice()
        loadingsheetdata()

        activityOrderSummaryBinding.shopName.text=HomeFragment.selectedShopName
        activityOrderSummaryBinding.placeOrder.isEnabled = true
        Log.d("TAG", "onCreateFiltered: $filtered")
        summaryCartList.clear()
        for (i in filtered){
            summaryCartList.add(i)
        }

        finalSummaryCartList.clear()
        if (finalSummaryCartList.isEmpty()) {
            finalSummaryCartList.add(summaryCartList[0])
        }

        for (j in finalSummaryCartList){
            for (k in summaryCartList){
                if (j.token != k.token){
                    finalSummaryCartList.add(k)
                }
            }
        }

        Log.d("TAG", "summaryCartList: $summaryCartList")

        totalAmountOnCreate()
        activityOrderSummaryBinding.addedProductList.adapter = selectedProductAdapter

        activityOrderSummaryBinding.back.setOnClickListener {

            onBackPressed()

        }
        activityOrderSummaryBinding.placeOrder.setOnClickListener {
//            activityOrderSummaryBinding.placeOrder.isEnabled = false
            getLastLocation()
            orderPlace()
        }
    }
    private fun loadingsheetdata() {
        loadingdata = ArrayList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    @SuppressLint("SetTextI18n")
    private val orderPlaceResponse = Observer<ServerResponse> {
        loadingDialog(false)
        System.out.println(it)

        authViewModel.locationList(
            Session.userToken,
            empLatitude,
            empLongitude,
            HomeFragment.selectedShopId
        )
    }
    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    private fun onClicked(data: ProductData.ProductListData){
        onRemovedPrice(data)
//        activityOrderSummaryBinding.totalItems.text="Total Items : "+TakeOrderActivity.cartdata.size.toString()
        if (finalSummaryCartList.size==0) {
            activityOrderSummaryBinding.noProductList.visibility=View.VISIBLE
        }else{
            activityOrderSummaryBinding.noProductList.visibility=View.GONE
        }
        activityOrderSummaryBinding.addedProductList.adapter!!.notifyDataSetChanged()
    }
    @SuppressLint("SetTextI18n")
    private fun totalAmount(){
        var total = 0.0

    }

    @SuppressLint("SetTextI18n")
    private fun totalAmountOnCreate(){

        Log.d("TAG", "totalAmountOnCreate: finalSummaryCartList $finalSummaryCartList")
        var total = 0.0
        if (finalSummaryCartList.size > 0){
            for (i in finalSummaryCartList){

                try {
                    total += i.total_value - (i.total_value * (i.percentage!!.toDouble() / 100))
                    Log.d("TAG", "totalAmountOnCreateTotal:  ${i.total_value}")

                }catch (e :Exception){
                    Log.d("TAG", "totalAmountOnCreate:  $e")
                }

            }
            val roundTotal = (total * 100.0).roundToInt() / 100.0
            activityOrderSummaryBinding.totalItems.text = "Total Items : ${finalSummaryCartList.size}"
            activityOrderSummaryBinding.totalPrice.text = "Total Price : ${applicationContext.getString(
                R.string.rupee)} " + roundTotal.toString()
            Log.d("TAG", "totalAmountOnCreate: $roundTotal")
        }else{
            activityOrderSummaryBinding.totalItems.text = "Total Items : ${finalSummaryCartList.size}"
            activityOrderSummaryBinding.totalPrice.text = "Total Price : ${applicationContext.getString(
                R.string.rupee)} " + 0.0
        }
    }


    private fun placeOrder() {
        placeOrderDialog.show()
        placeOrderDialog.findViewById<ImageButton>(R.id.closeRequestSuccess)?.setOnClickListener {
            placeOrderDialog.dismiss()

            startActivity(Intent(this, BottomNavigationActivity::class.java))
        }
        placeOrderDialog.findViewById<MaterialButton>(R.id.collectAmount)?.setOnClickListener {
            placeOrderDialog.dismiss()
            startActivity(Intent(this, BottomNavigationActivity::class.java))
        }
    }
    @SuppressLint("SetTextI18n")
    private fun onRemovedPrice(task: ProductData.ProductListData) {
//        if (task.IsAdditional_Available==true)
//        {
            val iterator = finalSummaryCartList.iterator()
            for(i in iterator){
                if(i.token== task.token){
                    iterator.remove()
                }
            }

        totalAmountOnCreate()

    }

    override fun onStart() {
        super.onStart()
        TakeOrderActivity.selectedScreen="OrderSummary"
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    empLatitude =  task.result.latitude
                   empLongitude =  task.result.longitude
                    val mLocationRequest = LocationRequest()
                    Log.d("TAG", "getLastLocation: $empLatitude and $empLongitude")
                    mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    mLocationRequest.interval = 6000000
                    mLocationRequest.fastestInterval = 6000000
                    mLocationRequest.numUpdates = 1000

                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    )
                    mFusedLocationClient.requestLocationUpdates(
                        mLocationRequest,  mLocationCallback,
                        Looper.myLooper()!!
                    )
                }
            } else {
                val builder = android.app.AlertDialog.Builder(this)
                builder.setMessage("Your GPS seems to be disabled, please enable it?")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, id ->
                        dialog.dismiss()
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
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


    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }


    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation: Location? = locationResult.lastLocation
            empLatitude = mLastLocation?.latitude!!
            empLongitude = mLastLocation?.longitude!!

            Log.d("TAG", "onLocationResult empLatitude: $empLatitude")
            Log.d("TAG", "onLocationResult empLongitude: $empLongitude")

            if (Session.userToken.isEmpty()){
                Toast.makeText(this@OrderSummaryActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }else if (empLatitude == 0.0 && empLongitude == 0.0){
                Toast.makeText(this@OrderSummaryActivity, "Please Enable Location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun orderPlace(){
        if (finalSummaryCartList.isNotEmpty())
        {
            val OrderSummarydata: ArrayList<ProductData.ProductListData> = ArrayList()

            OrderSummarydata.addAll(finalSummaryCartList)

            Log.d("TAG", "orderPlace: $finalSummaryCartList")

            if(type == "edit"){
                type = "fresh"
                Log.d("TAG", "orderPlace: edit")
                val saveParameterJson = JsonObject().apply {
                    addProperty("salesPersonId", Session.userToken)
                    addProperty("billAmount", ShopDetailActivity.loadTotalAmount)
                    addProperty("shopToken", HomeFragment.selectedShopId)
                    addProperty("order_id", orderId)
                    add("data", Gson().toJsonTree(OrderSummarydata))
                }
                Log.d("saveParameterJson", "onLocationResult: $saveParameterJson")
                if (isNetworkConnected(this@OrderSummaryActivity)) {
                    loadingDialog(true)
                    lifecycleScope.launchWhenResumed {
                        homeViewModel.orderUpdate(saveParameterJson)
                    }
                }
                else startActivity(Intent(this@OrderSummaryActivity, NoInternetActivity::class.java))
            } else{
                type = ""
                val saveParameterJson = JsonObject().apply {
                    addProperty("salesPersonId", Session.userToken)
                    addProperty("billAmount", ShopDetailActivity.loadTotalAmount)
                    addProperty("shopToken", HomeFragment.selectedShopId)
                    add("data", Gson().toJsonTree(OrderSummarydata))
                }
                Log.d("saveParameterJson", "onLocationResult: $saveParameterJson")
                if (isNetworkConnected(this@OrderSummaryActivity)) {
                    loadingDialog(true)
                    lifecycleScope.launchWhenResumed {
                        homeViewModel.placeOrder(saveParameterJson)
                    }
                }
                else startActivity(Intent(this@OrderSummaryActivity, NoInternetActivity::class.java))
            }

        }
        else {
            Toast.makeText(this@OrderSummaryActivity, "Please add a products to place order", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("SetTextI18n")
    private val productResponse = Observer<LocationData> {
        if (it.status_code == 200) {
            isPlaceOrder = true
            Session.shopId = 0
            Log.d("TAG", "SessionshopId: ${Session.shopId}")
            placeOrder()
        }
    }
}