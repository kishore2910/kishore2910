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
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.*
import com.powersoaps.distributorsales.databinding.ActivityOrderDetailsBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.adapter.OrderProductAdapter
import com.powersoaps.distributorsales.ui.main.adapter.PaymentDetailsAdapter
import com.powersoaps.distributorsales.ui.utils.DividerItemDecorator
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.text.DecimalFormat

class OrderDetailsActivity : BaseActivity() {

    private val reqSuccessDialog by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.bottompaidsheet, null))
        setCancelable(false)} }


    private val success by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.dialog_order_complete, null))
        setCancelable(false)} }

    val paymentdetails = ArrayList<PaymentDetailsData.dataobject.Overall_payment_details>()
    private lateinit var loadingdata: ArrayList<SummaryData>

    private val homeViewModel: HomeViewModel by viewModels()

    private val loadingSheetAdapter by lazy { OrderProductAdapter(orderdetailsdata) }

    private val detailsBinding by lazy { ActivityOrderDetailsBinding.inflate(layoutInflater) }

    private var  reasonsize:Int =0
    private var ordertoken : String = ""
    private var type : String = "Cash"
    private var amount = ""
    private var orderdate = ""
    private var outstandvalue : String = ""
    private var amttag : String = ""
    private var amtpaidtag : String = ""
    private var outtag : String = ""
    private var orderstatus : String=""

    val PERMISSION_ID = 42
    lateinit var mFusedLocationClient: FusedLocationProviderClient


    private val authViewModel: AuthViewModel by viewModels()



    companion object
    {
        private lateinit var orderdetailsdata: ArrayList<OrderDetailsData.OrderProductList.OrderProductData>
        var empLatitude:Double=0.0
        var empLongitude:Double=0.0
        var pendingText : String = "Pending"

    }
     lateinit var shoptoken : String
     lateinit var orderid : String

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(detailsBinding.root)

        val bundle = intent.extras
        if (bundle != null) {
            shoptoken = bundle.getString("shoptoken").toString()
            orderid = bundle.getString("orderid").toString()
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        loadingsheetdata()
        homeViewModel.OrderDetailsData.observe(this@OrderDetailsActivity, OrderHistoryResponse)
        homeViewModel.paymentdata.observe(this@OrderDetailsActivity, PaymentDetailsResponse)
        homeViewModel.ordertransdata.observe(this@OrderDetailsActivity, OrderTransactionResponse)


        authViewModel.LocationData.observe(this, productResponse)


        if (isNetworkConnected(this)) {
            loadingDialog(true)
            homeViewModel.OrderDetailsApi(Session.userToken,shoptoken,orderid)
        }
        else startActivity(Intent(this, NoInternetActivity::class.java))


        //get Bank Details
        getBankDetails()

//        homeViewModel.orderdeliverdata.observe(this@OrderDetailsActivity, OrderdeliveryResponse)

        homeViewModel.bankDetailResponse.observe(this@OrderDetailsActivity, bankDetailsResponse)


        detailsBinding.back.setOnDebounceListener {
            onBackPressed()
        }

//
//        detailsBinding.deliverorder.setOnDebounceListener {
//
//            getLastLocation()
//
//
////            detailsBinding.viewincompleteline.visibility = View.INVISIBLE
////            detailsBinding.viewline.visibility = View.VISIBLE
////            detailsBinding.deliverincomplete.visibility = View.INVISIBLE
////            detailsBinding.delivercomplete.visibility = View.VISIBLE
////            detailsBinding.delivercompletetick.visibility = View.VISIBLE
////            detailsBinding.deliverorder.visibility = View.GONE
////            detailsBinding.collectamount.visibility = View.VISIBLE
//        }




        detailsBinding.collectamount.setOnDebounceListener {
            reqSuccessDialog.show()
            type = "Cash"
            val paidsiable = reqSuccessDialog.findViewById<Button>(R.id.paidcustomerdisable)
            val paidenable = reqSuccessDialog.findViewById<Button>(R.id.paidcustomer)
            val orderid = reqSuccessDialog.findViewById<TextView>(R.id.orderid)
            val date = reqSuccessDialog.findViewById<TextView>(R.id.date)
            val total = reqSuccessDialog.findViewById<TextView>(R.id.amounttag)
            val amountpaid = reqSuccessDialog.findViewById<TextView>(R.id.amtpaidtag)
            val outstanding = reqSuccessDialog.findViewById<TextView>(R.id.outtag)
            orderid?.setText("Order ID :"+" "+ordertoken)
            date?.setText("Ordered on :"+" "+orderdate)
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = amttag.toDouble() //If your num is in String
            val currency = fmt.format(billamount)
            val billamountone: Double = amtpaidtag.toDouble() //If your num is in String
            val currencyone = fmt.format(billamountone)
            val billamounttwo: Double =outtag.toDouble() //If your num is in String
            val currencytwo = fmt.format(billamounttwo)
            total?.text="₹"+" "+currency
            amountpaid?.text="₹"+" "+currencyone
            outstanding?.text="₹"+" "+currencytwo
            reqSuccessDialog.findViewById<ImageButton>(R.id.closeRequestSuccess)?.setOnClickListener {
                reqSuccessDialog.dismiss()
            }
            reqSuccessDialog.findViewById<Button>(R.id.chequeinactive)?.setOnClickListener {
                type = "Cheque"
                reqSuccessDialog.findViewById<ConstraintLayout>(R.id.showUpi)!!.visibility = View.GONE
                reqSuccessDialog.findViewById<Button>(R.id.paidcustomer)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<ConstraintLayout>(R.id.mobilelayout)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cheque)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.chequeinactive)!!.visibility = View.INVISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cashinactive)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cash)!!.visibility = View.INVISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.upiinactive)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.upi)!!.visibility = View.INVISIBLE
            }
            reqSuccessDialog.findViewById<Button>(R.id.cashinactive)?.setOnClickListener {
                type = "Cash"
                reqSuccessDialog.findViewById<ConstraintLayout>(R.id.showUpi)!!.visibility = View.GONE
                reqSuccessDialog.findViewById<Button>(R.id.paidcustomer)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<ConstraintLayout>(R.id.mobilelayout)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cheque)!!.visibility = View.INVISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.chequeinactive)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cashinactive)!!.visibility = View.INVISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cash)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.upiinactive)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.upi)!!.visibility = View.INVISIBLE
            }
            reqSuccessDialog.findViewById<Button>(R.id.upiinactive)?.setOnClickListener{
                type = "Upi"
                reqSuccessDialog.findViewById<ConstraintLayout>(R.id.mobilelayout)!!.visibility = View.GONE
                reqSuccessDialog.findViewById<Button>(R.id.paidcustomerdisable)!!.visibility = View.GONE
//                reqSuccessDialog.findViewById<Button>(R.id.paidcustomer)!!.visibility = View.GONE
                reqSuccessDialog.findViewById<ConstraintLayout>(R.id.showUpi)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cheque)!!.visibility = View.INVISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.chequeinactive)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cashinactive)!!.visibility = View.VISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.cash)!!.visibility = View.INVISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.upiinactive)!!.visibility = View.INVISIBLE
                reqSuccessDialog.findViewById<Button>(R.id.upi)!!.visibility = View.VISIBLE
                paidenable?.visibility = View.GONE
            }


            reqSuccessDialog.findViewById<EditText>(R.id.rupee)?.addTextChangedListener(object :
                TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    try {
                        if (s.toString().isNotEmpty()) {
                            val usercount=s.toString().toLong()
                            val availablecount = outstandvalue.toInt()
                            val resultcount=availablecount-usercount
                            if (resultcount >= 0) {
                                paidsiable?.visibility = View.GONE
                                paidenable?.visibility = View.VISIBLE
                            }else{
                                paidsiable?.visibility = View.VISIBLE
                                paidenable?.visibility = View.GONE
                                Toast.makeText(this@OrderDetailsActivity, "please enter correct amount", Toast.LENGTH_SHORT).show()
                            }

                        }else{
                            paidsiable?.visibility = View.VISIBLE
                            paidenable?.visibility = View.GONE
                        }
                    } catch (e: NumberFormatException) {
//                        Toast.makeText(this@OrderDetailsActivity, "please enter correct amount", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun afterTextChanged(s: Editable?) {

                }
            })


            reqSuccessDialog.findViewById<Button>(R.id.paidcustomer)?.setOnClickListener {
                amount = reqSuccessDialog.findViewById<EditText>(R.id.rupee)?.text.toString().trim()
                trans(amount)
            }
        }

        detailsBinding.viewmore.setOnDebounceListener {
            reasonsize=orderdetailsdata.size
            detailsBinding.viewmore.visibility = View.INVISIBLE
            detailsBinding.viewless.visibility = View.VISIBLE
            detailsBinding.detaillist.adapter = loadingSheetAdapter
        }

        detailsBinding.viewless.setOnDebounceListener {
            if (orderdetailsdata.size > 4) {
                reasonsize = 4
                detailsBinding.viewmore.visibility = View.VISIBLE
                detailsBinding.viewless.visibility = View.INVISIBLE
                detailsBinding.detaillist.adapter = loadingSheetAdapter
            } else {
                reasonsize=orderdetailsdata.size
                detailsBinding.viewmore.visibility = View.INVISIBLE
                detailsBinding.viewless.visibility = View.VISIBLE
                detailsBinding.detaillist.adapter = loadingSheetAdapter
            }
        }
    }


    private fun getBankDetails(){
        val jsonObject = JsonObject().apply {
            addProperty("salesman_token",Session.userToken)
        }
        homeViewModel.bankDetails(jsonObject)
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
                    val location: Location? = task.result
//                    initTab()
//                    if (location == null) {
//                        requestNewLocationData()
//                    } else {
//                        requestNewLocationData()
//                        Utility.TechnicianLatitude = location.latitude
//                        Utility.TechnicianLongitude = location.longitude
//                        insertDeviceInfo()
//                    }
                    val mLocationRequest = LocationRequest()
                    mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    mLocationRequest.interval = 6000000
                    mLocationRequest.fastestInterval = 6000000
                    mLocationRequest.numUpdates = 1000

                    mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
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
            if (Session.userToken.isEmpty()) {
                Toast.makeText(this@OrderDetailsActivity,
                    "Something went wrong",
                    Toast.LENGTH_SHORT).show()
            } else if (empLatitude == 0.0 && empLongitude == 0.0) {
                Toast.makeText(this@OrderDetailsActivity,
                    "Please Enable Location",
                    Toast.LENGTH_SHORT).show()
            } else {
                authViewModel.locationList(Session.userToken,
                    empLatitude,
                    empLongitude,shoptoken
                )
                // lat lng
            }
        }
    }


    private fun completedeliver() {
        if (isNetworkConnected(this)) {
            loadingDialog(true)
            homeViewModel.OrderDeliver(Session.userToken,shoptoken,orderid,pendingText)
        }
        else startActivity(Intent(this, NoInternetActivity::class.java))
    }

    private fun loadingsheetdata() {
        loadingdata = ArrayList()
        orderdetailsdata = ArrayList()
    }

    fun RecyclerView.addItemDecorationWithoutLastItem() {
        if (layoutManager !is LinearLayoutManager)
            return
        addItemDecoration(DividerItemDecorator(context))
    }


    @SuppressLint("SetTextI18n")
    private val OrderHistoryResponse = Observer<OrderDetailsData> {
        loadingDialog(false)
        if (it.status_code == 200) {
            System.out.println(it)
            for (a in 0 until it.data!!.order_details!!.size) {
                orderdetailsdata.add(
                    OrderDetailsData.OrderProductList.OrderProductData(
                        it.data.order_details!![a].product_token,it.data.order_details[a].quantity,
                        it.data.order_details[a].units,it.data.order_details[a].misc_price,
                        it.data.order_details[a].product_name,
                        it.data.order_details[a].percentage_value, it.data.order_details[a].amount,
                        it.data.order_details[a].is_discount_enable, it.data.order_details[a].is_free
                ) )
            }
//            if (orderdetailsdata.size > 4) {
//                reasonsize = 4
//                detailsBinding.viewmore.visibility = View.VISIBLE
//                detailsBinding.detaillist.adapter = loadingSheetAdapter
//            }
//            else {
//                reasonsize=orderdetailsdata.size
//                detailsBinding.viewmore.visibility = View.INVISIBLE
//                detailsBinding.detaillist.adapter = loadingSheetAdapter
//            }
            detailsBinding.detaillist.adapter = loadingSheetAdapter
            orderstatus=it.data.order_val!!.order_status.toString()
            if (it.data.order_val.order_status == "Completed"){
                detailsBinding.viewincompleteline.visibility = View.INVISIBLE
                detailsBinding.viewline.visibility = View.VISIBLE
                detailsBinding.deliverincomplete.visibility = View.INVISIBLE
                detailsBinding.delivercomplete.visibility = View.VISIBLE
                detailsBinding.delivercompletetick.visibility = View.VISIBLE
                detailsBinding.deliverorder.visibility = View.GONE
                detailsBinding.deliverdate.visibility = View.VISIBLE
                detailsBinding.delivername.visibility = View.VISIBLE
                detailsBinding.cancelremover.visibility = View.GONE
                detailsBinding.cancel.visibility = View.GONE
                detailsBinding.bottomsheet.visibility = View.VISIBLE
                detailsBinding.delivername.text ="Delivered by"+" "+it.data.order_val.employee_name
                detailsBinding.deliverdate.text ="Delivered on"+" "+it.data.order_val.order_delivery_date
                detailsBinding.collectamount.visibility = View.VISIBLE
            }
            else if(it.data.order_val.order_status == "Pending"){
                detailsBinding.viewincompleteline.visibility = View.VISIBLE
                detailsBinding.viewline.visibility = View.INVISIBLE
                detailsBinding.deliverincomplete.visibility = View.VISIBLE
                detailsBinding.delivercomplete.visibility = View.INVISIBLE
                detailsBinding.delivercompletetick.visibility = View.INVISIBLE
                detailsBinding.deliverorder.visibility = View.GONE                ////next release
                detailsBinding.deliverdate.visibility = View.VISIBLE
                detailsBinding.delivername.visibility = View.VISIBLE
                detailsBinding.cancelremover.visibility = View.GONE
                detailsBinding.cancel.visibility = View.GONE
                detailsBinding.collectamount.visibility = View.VISIBLE
            }else{
                detailsBinding.cancel.visibility = View.VISIBLE
                detailsBinding.cancelremover.visibility = View.VISIBLE
                detailsBinding.deliverdate.visibility = View.VISIBLE
                detailsBinding.delivername.visibility = View.GONE
                detailsBinding.deliverorder.visibility = View.GONE
                detailsBinding.collectamount.visibility = View.GONE
                detailsBinding.deliverdate.text ="Cancelled on"+" "+it.data.order_val.order_delivery_date
            }


            val fmtpro = DecimalFormat("#,##,###.##")
            val billamountpro: Double = it.data.order_val.pro_amount!!.toDouble() //If your num is in String
            val currencypro = fmtpro.format(billamountpro)
            detailsBinding.amount.text = "₹"+" "+currencypro
            detailsBinding.ordername.text =it.data.order_val.shop_name
//                orderDetails.delivername.text ="Delivered by"+" "+it.data.order_val.shop_name

            val fmtgst = DecimalFormat("#,##,###.##")
            val billamountgst: Double = it.data.order_val.gst!!.toDouble() //If your num is in String
            val currencygst = fmtgst.format(billamountgst)
            detailsBinding.gst.text = "₹"+" "+currencygst
//            detailsBinding.totalamount.text = orderdetailsdata.size.toString()
            detailsBinding.items.text = "| Items : "+it.data.order_val.order_items.toString()
            detailsBinding.orderdate.text = "Ordered on"+" "+it.data.order_val.order_schedule_date.toString()
            detailsBinding.ordercount.text = "Order ID :"+" "+it.data.order_val.order_token.toString()

            ordertoken = it.data.order_val.order_token.toString()
            orderdate = it.data.order_val.order_schedule_date.toString()

//            if (it.data.order_val!!.discount_amount_finall=="0" )
//            {
//                detailsBinding.step1line.visibility=View.GONE
//                detailsBinding.amountheading.visibility=View.GONE
//                detailsBinding.amount.visibility=View.GONE
//                detailsBinding.discount.visibility=View.GONE
//            }
//            else
//            {
//                detailsBinding.step1line.visibility=View.VISIBLE
//                detailsBinding.amountheading.visibility=View.VISIBLE
//                detailsBinding.amount.visibility=View.VISIBLE
//                detailsBinding.discount.visibility=View.VISIBLE
//                detailsBinding.discount.text="Discount( "+it.data.order_val.offer_value+" )"
//                detailsBinding.totalamount.text = "₹"+" "+String.format("%.1f", it.data.order_val.discount_amount_finall?.toDouble())
//            }
            val fmtamt = DecimalFormat("#,##,###.##")
            val billamountamt: Double = it.data.order_val.over_all_amount!!.toDouble() //If your num is in String
            val currencydisamt = fmtamt.format(billamountamt)
            detailsBinding.amount.text = "₹"+" "+currencydisamt
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = it.data.order_val.total_amount!!.toDouble() //If your num is in String
            val currency = fmt.format(billamount)//new
            detailsBinding.totalamount.text = "₹"+" "+currency //new
            val fmtdis = DecimalFormat("#,##,###.##")
            val billamountdis: Double = it.data.order_val.discount_new_value!!.toDouble() //If your num is in String
            val currencydis = fmtdis.format(billamountdis)
            detailsBinding.discountTextview.text = "₹"+" "+currencydis //new


            if(it.data.order_val.percentage_offer_value!!.toInt() <= 0 ){
                detailsBinding.amountLayout.visibility=View.GONE
                detailsBinding.totalLayout.visibility=View.VISIBLE
                detailsBinding.discountLayout.visibility = View.GONE
            }else{
                detailsBinding.amountLayout.visibility=View.VISIBLE
                detailsBinding.totalLayout.visibility=View.VISIBLE
                detailsBinding.discountLayout.visibility = View.VISIBLE
            }

        }

        else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
        homeViewModel.PaymentDetailsApi(Session.userToken,shoptoken,orderid)
    }
    private val PaymentDetailsResponse = Observer<PaymentDetailsData> {
        print(it)
        if (it.status_code==200) {
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = it.data!!.amout_data.billing_amount.toDouble() //If your num is in String
            val currency = fmt.format(billamount)
            val billamountone: Double = it.data!!.amout_data.paid_amount.toDouble() //If your num is in String
            val currencyone = fmt.format(billamountone)
            val billamounttwo: Double = it.data!!.amout_data.outstanding_amount.toDouble() //If your num is in String
            val currencytwo = fmt.format(billamounttwo)
            detailsBinding.totalamt.text="₹"+" "+currency
            detailsBinding.amtpaid.text="₹"+" "+currencyone
            detailsBinding.outstandingamt.text="₹"+" "+currencytwo

            amttag = it.data!!.amout_data.billing_amount
            amtpaidtag = it.data!!.amout_data.paid_amount
            outtag = it.data!!.amout_data.outstanding_amount
            outstandvalue = it.data!!.amout_data.outstanding_amount
            if (outstandvalue == "0"){
                detailsBinding.collectamount.visibility = View.GONE
            }else{
                if(orderstatus=="Cancelled") detailsBinding.collectamount.visibility = View.GONE
                else detailsBinding.collectamount.visibility = View.VISIBLE
            }
            paymentdetails.addAll(it.data!!.overall_payment_details)
            detailsBinding.paymentlist.adapter = PaymentDetailsAdapter(paymentdetails)
        }
        else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    private val OrderTransactionResponse = Observer<ServerResponse> {
        loadingDialog(false)
        if (it.status_code==200) {
            Toast.makeText(this, "Amount Collected Successfully", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, ShopDetailActivity::class.java))
            finish()
        }
        else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }




    @SuppressLint("SetTextI18n")
    private val bankDetailsResponse = Observer<BanksDetailsData>{
        if (it.statusCode == 200){
            Log.d("TAG", "bankDetails: ${it.data?.bankCode}")
            val gpay = reqSuccessDialog.findViewById<TextView>(R.id.gpay)
            val paytm = reqSuccessDialog.findViewById<TextView>(R.id.paytm)
            val bankName = reqSuccessDialog.findViewById<TextView>(R.id.bankName)
            val acNumber = reqSuccessDialog.findViewById<TextView>(R.id.acNumber)
            val bankCode = reqSuccessDialog.findViewById<TextView>(R.id.bankCode)
            val holder = reqSuccessDialog.findViewById<TextView>(R.id.holder)
            gpay?.text = "Gpay Number : ${it.data?.gpayNumber}"
            paytm?.text = "Paytm Number : ${it.data?.paytmNumber}"
            bankName?.text = "Bank name : ${it.data?.bankName}"
            acNumber?.text = "account Number : ${it.data?.accountNumber}"
            bankCode?.text = "Bank Code : ${it.data?.bankCode}"
            holder?.text = "Holder name : ${it.data?.holderName}"
        }else {
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

//    private val OrderdeliveryResponse = Observer<ServerResponse> {
//        loadingDialog(false)
//        if (it.status_code==200) {
////            Toast.makeText(this, "Successfully Delivered", Toast.LENGTH_SHORT).show()
//            startActivity(Intent(this, ShopDetailActivity::class.java))
//            finish()
//        } else {
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
//        }
//    }


    @SuppressLint("SetTextI18n")
    private val productResponse = Observer<LocationData> {
        if (it.status_code == 200) {
            placeOrder()
        }
    }

    private fun placeOrder() {
        success.show()

        success.findViewById<TextView>(R.id.takeOrdersTextView)?.text = "Order Delivered Successfully"
        success.findViewById<ImageButton>(R.id.closeRequestSuccess)?.setOnClickListener {
            success.dismiss()
        }
//        Toast.makeText(this, "Order Delivered Successfully", Toast.LENGTH_SHORT).show()
        success.findViewById<MaterialButton>(R.id.collectAmount)?.setOnClickListener {
            completedeliver()
        }
    }


    private fun trans(amount: String) {
        loadingDialog(true)
        homeViewModel.OrderTransactionApi(Session.userToken,shoptoken,orderid,amount,type)
    }

}