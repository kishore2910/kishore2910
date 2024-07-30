package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.PreviousOrderData
import com.powersoaps.distributorsales.data.model.ShopDetailData
import com.powersoaps.distributorsales.data.model.ShopVisitData
import com.powersoaps.distributorsales.databinding.ActivityShopDetailBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.activity.introscreens.ForgetPassword
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.text.DecimalFormat
import kotlin.math.roundToInt

class ShopDetailActivity : BaseActivity() {

    private val shopBinding by lazy { ActivityShopDetailBinding.inflate(layoutInflater) }

    private val orderOptionDialog by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.bottom_take_order, null))
        setCancelable(false)} }

    private val homeViewModel: HomeViewModel by viewModels()
    private val authViewModel : AuthViewModel by viewModels()
    lateinit var mFusedLocationClient: FusedLocationProviderClient
    val PERMISSION_ID = 42

    companion object
    {
        lateinit var loadPreviousData: ArrayList<PreviousOrderData.PreviousOrderList.OrderProductDetails>
        lateinit var loadTotalAmount: String
        lateinit var ShopMobileNumber: String
        lateinit var ShopLatLong: String
        lateinit var shopstatus: String
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(shopBinding.root)
        shopBinding.shopName.text=HomeFragment.selectedShopName
        shopBinding.category.text=HomeFragment.selectedShopType
        shopBinding.kms.text=HomeFragment.selectedUnitName
        shopBinding.kms.ellipsize = TextUtils.TruncateAt.END
        shopBinding.kms.isSingleLine = true
        homeViewModel.shopDetailData.observe(this, shopDetailResponse)
        homeViewModel.previousOrderData.observe(this@ShopDetailActivity, previousOrderResponse)
        authViewModel.shopVisitedData.observe(this@ShopDetailActivity, shopVisitedResponse)

        //details
        if (isNetworkConnected(this)) {
            loadingDialog(true)
            homeViewModel.shopDetails(Session.userToken,HomeFragment.selectedShopId)
        }
        else startActivity(Intent(this, NoInternetActivity::class.java))


        //previous
        if (isNetworkConnected(this))
        {
            loadingDialog(true)
            homeViewModel.previousOrderData(Session.userToken,HomeFragment.selectedShopId)
        }
        else startActivity(Intent(this, NoInternetActivity::class.java))

        shopBinding.shopInSight.setOnDebounceListener {
            startActivity(Intent(this@ShopDetailActivity, ShopInSightsActivity::class.java))
        }
        shopBinding.orderHistory.setOnDebounceListener {
            startActivity(Intent(this@ShopDetailActivity, OrderHistoryActivity::class.java))
        }
        shopBinding.takeOrders.setOnClickListener {
            Location()
        }
        shopBinding.back.setOnClickListener {
            startActivity(Intent(this,BottomNavigationActivity::class.java))
        }
        shopBinding.callStore.setOnClickListener {
            AlertCallDialogue()
        }
        shopBinding.collectorder.setOnClickListener {
            startActivity(Intent(this, OrderCollections::class.java).putExtra("shopname",HomeFragment.selectedShopName))
        }
        shopBinding.returnstore.setOnClickListener {
            startActivity(Intent(this, SalesReturn::class.java).putExtra("shopname",HomeFragment.selectedShopName))
        }
        shopBinding.viewonMap.setOnClickListener {
            val strUri = "http://maps.google.com/maps?q=loc:" +ShopLatLong+ " (" + HomeFragment.selectedShopName + ")"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strUri))
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity")
            startActivity(intent)
        }
        shopBinding.aboutShop.setOnClickListener {
            startActivity(Intent(this@ShopDetailActivity, AboutStoreActivity::class.java))
        }

        shopBinding.visited.setOnClickListener {
            visited()
        }


    }

    private fun Location(){
        if (isLocationEnabled()){
            startActivity(Intent(this@ShopDetailActivity, TakeOrderActivity::class.java).putExtra("type","fresh"))
        }else{
            showLocationEnabled()
        }
    }

    private fun showLocationEnabled(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Kindly turn on your Location")
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
                startActivity(Intent(this@ShopDetailActivity, TakeOrderActivity::class.java).putExtra("type","fresh"))
            }
        val alert = builder.create()
        alert.show()
    }

    private fun visited(){
        if (isNetworkConnected(this)){
            val jsonObject = JsonObject().apply {
                addProperty("shop_id", HomeFragment.selectedShopId)
                addProperty("employee_id",Session.userToken)
                addProperty("type","1")
                addProperty("unit_name",HomeFragment.selectedUnitName)

            }
            Log.d("TAG", "visited: $jsonObject")
            authViewModel.shopVisited(jsonObject)
        }else {
            Toast.makeText(this,"Please turn on ur internet", Toast.LENGTH_SHORT).show()
        }

    }

    private fun bottomSheet() {
        val loadPrevious = orderOptionDialog.findViewById<Button>(R.id.loadPrevious)
        val takeFreshOrder = orderOptionDialog.findViewById<Button>(R.id.takeFreshOrder)
        val close = orderOptionDialog.findViewById<ImageButton>(R.id.closeRequestSuccess)
        val date = orderOptionDialog.findViewById<AppCompatTextView>(R.id.date)
        loadPrevious?.setOnClickListener {
            orderOptionDialog.dismiss()
//            loadingDialog(true)
//            homeViewModel.previousOrderData(Session.userToken)
            startActivity(Intent(this@ShopDetailActivity, TakeOrderActivity::class.java).putExtra("type","previous"))

        }
        takeFreshOrder?.setOnClickListener {
            orderOptionDialog.dismiss()
            startActivity(Intent(this@ShopDetailActivity, TakeOrderActivity::class.java).putExtra("type","fresh"))
        }
        close?.setOnClickListener {
            orderOptionDialog.dismiss()
        }
        if (loadPreviousData.size==0) {
            loadPrevious!!.visibility=View.GONE
            date!!.visibility=View.GONE
        } else {
            loadPrevious!!.visibility=View.VISIBLE
            date!!.visibility=View.VISIBLE
        }
        orderOptionDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this,BottomNavigationActivity::class.java))
        finish()

    }

    @SuppressLint("SetTextI18n")
    private val shopDetailResponse = Observer<ShopDetailData> {
        try{
            loadingDialog(false)
            System.out.println(it)
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = it.data!!.bill_amount.toDouble() //If your num is in String
            val currency = fmt.format(billamount)
            val billamountone: Double = it.data.paid_amt.toDouble() //If your num is in String
            val currencyone = fmt.format(billamountone)
            val billamounttwo: Double = it.data.total_outstanding.toDouble() //If your num is in String
            val currencytwo = fmt.format(billamounttwo)
            shopBinding.totalSales.text=shopBinding.root.context.getString(R.string.rupee)+" "+currency
            shopBinding.totalPaid.text=shopBinding.root.context.getString(R.string.rupee)+" "+currencyone
            shopBinding.outstandingAmount.text=shopBinding.root.context.getString(R.string.rupee)+" "+currencytwo
//        shopBinding.kms.text=it.data.city
            ShopMobileNumber=it.data.mobile_number
            ShopLatLong=it.data.coordinates
            shopstatus=it.data.shop_status
        }catch (shopDetailException : Exception){
            Log.d("TAG", "shopDetailException: $shopDetailException ")
        }
//        loadPreviousData= ArrayList()
//        loadTotalAmount=""

    }
    @SuppressLint("SetTextI18n")
    private val previousOrderResponse = Observer<PreviousOrderData> {
        loadPreviousData= ArrayList()
        loadingDialog(false)
        var totalPrice:Double=0.0
        if (it.status_code == 200) {
            System.out.println(it)

            Log.d("TAG", "check_tokenSize:${ it.data!!.order_details!!.size}")
            for (a in 0 until it.data!!.order_details!!.size) {

                Log.d("TAG", "check_token:${ it.data.order_details!![a].product_token}")
                loadPreviousData.add(PreviousOrderData.PreviousOrderList.OrderProductDetails(
                    it.data.order_details!![a].product_token,it.data.order_details[a].product_name,
                    it.data.order_details!![a].quantity,it.data.order_details[a].units,
                    it.data.order_details!![a].amount,it.data.order_details!![a].piece_count,
                ) )
                val productPrice:Double=  it.data.order_details!![a].amount!!.toDouble()
                if (it.data.order_details[a].units=="Nos")
                {
                    totalPrice=totalPrice+(productPrice*it.data.order_details!![a].quantity!!.toDouble())
//                    totalPrice=totalPrice+(productPrice)
                }
                else {
                    totalPrice=totalPrice+(productPrice*it.data.order_details!![a].quantity!!.toDouble()*it.data.order_details!![a].piece_count!!.toDouble())
//                    totalPrice=totalPrice+(productPrice)
                }
            }
            System.out.println(loadPreviousData)
//            loadTotalAmount=String.format("%.1f", totalPrice)
//            loadTotalAmount=(Math.round(totalPrice * 10.0) / 10.0).toInt().toString()
            loadTotalAmount=totalPrice.roundToInt().toString()



        }
//        else {
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//        }
//        if (shopstatus.equals("Inactive")) {
//            Toast.makeText(this, "Shop Deactivated", Toast.LENGTH_SHORT).show()
//        }
//        else
//            bottomSheet()
    }

    private val shopVisitedResponse = Observer<ShopVisitData>{
        if(it.statusCode == 200){
            Log.d("TAG", "statusCode: 200")
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }else{
            Log.d("TAG", "statusCode: 400")
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
    private fun AlertCallDialogue() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure you need to contact store?")
            .setCancelable(false)
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+ShopMobileNumber)
                startActivity(intent)
            })
            .show()
    }

    @SuppressLint("SuspiciousIndentation")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    val mLocationRequest = LocationRequest()
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
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
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
            OrderSummaryActivity.empLatitude = mLastLocation?.latitude!!
            OrderSummaryActivity.empLongitude = mLastLocation?.longitude!!

            Log.d("TAG", "onLocationResult empLatitude: ${OrderSummaryActivity.empLatitude}")
            Log.d("TAG", "onLocationResult empLongitude: ${OrderSummaryActivity.empLongitude}")

            if (Session.userToken.isEmpty()){
                Toast.makeText(this@ShopDetailActivity, "Something went wrong", Toast.LENGTH_SHORT).show()
            }else if (OrderSummaryActivity.empLatitude == 0.0 && OrderSummaryActivity.empLongitude == 0.0){
                Toast.makeText(this@ShopDetailActivity, "Please Enable Location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shopBinding.shopName.text=HomeFragment.selectedShopName
    }
}