package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.powersoaps.distributorsales.data.model.ShopTypeData
import com.powersoaps.distributorsales.databinding.ActivityAddNewShopBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatTextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.adapter.SpinnerAdapter
import com.powersoaps.distributorsales.ui.utils.AWSFileUploader
import com.powersoaps.distributorsales.ui.utils.AwsConstants
import com.powersoaps.distributorsales.ui.utils.Session
import java.lang.reflect.Method
import kotlin.collections.ArrayList

class AddNewShopActivity : AppCompatActivity(),AWSFileUploader.AWSFileUploadListener, PickiTCallbacks {


    private val addnewshopBinding by lazy { ActivityAddNewShopBinding.inflate(layoutInflater) }


    lateinit private var resultLauncher: ActivityResultLauncher<Intent>;
    lateinit private var resultLauncherOne: ActivityResultLauncher<Intent>;

    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lastLocation: Location? = null

    private var spinner:ArrayList<ShopTypeData.ShopListData> ? = null

    private val addNewShopOrderDialog by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.dialog_order_complete, null))
        setCancelable(false)} }
    private val chooseUploadDialog by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.bottom_upload_choose, null))
        setCancelable(false)} }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var  pickiT: PickiT;
        lateinit var  fileName: String;
        lateinit var  selectedShopType: String;
        var  selectedUrl: String="";
        private val REQUEST_PERMISSION = 100
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(addnewshopBinding.root)
        pickiT = PickiT(this, this, this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        homeViewModel.ShopTypeData.observe(this@AddNewShopActivity,ShopTypeResponse)
        homeViewModel.AddShopData.observe(this@AddNewShopActivity,AddShopResponse)

        homeViewModel.ShopTypeApi()

        addnewshopBinding.back.setOnDebounceListener {
            onBackPressed()
        }

        addnewshopBinding.uploadImage.setOnDebounceListener {
            Readpermissions()
        }
        addnewshopBinding.coordinates.setOnDebounceListener {
            getLocation()
        }
        addnewshopBinding.saveShop.setOnDebounceListener {
            try {
                lateinit var sLat:String
                val sName=addnewshopBinding.shopName.text.toString()
                val cPerson=addnewshopBinding.contactPerson.text.toString()
                val cNumber=addnewshopBinding.contactNumber.text.toString()
                val sType=addnewshopBinding.shopType.text.trim().toString()
                val licenseNumber=addnewshopBinding.licenseNumber.text.toString()
                val sAddress=addnewshopBinding.address.text.toString()
                val sCity=addnewshopBinding.city.text.toString()
                val sPincode=addnewshopBinding.pincode.text.toString()
                val coord=addnewshopBinding.coordinates.text.toString()
                sLat = if (lastLocation!=null){
                    (lastLocation)!!.latitude.toString()+","+(lastLocation)!!.longitude.toString()
                } else{
                    "0.0"
                }


                if (sName.isEmpty()) {
                    Toast.makeText(this, "Enter Shop Name", Toast.LENGTH_SHORT).show()
                }

                else if (cNumber.isEmpty())
                {
                    Toast.makeText(this, "Enter Contact Person Number", Toast.LENGTH_SHORT).show()
                }
                else if (cNumber.length<10) {
                    Toast.makeText(this, "Enter 10 Digit Contact Number", Toast.LENGTH_SHORT).show()
                }

                else if (sAddress.isEmpty()) {
                    Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show()
                }
                else if (sCity.isEmpty()) {
                    Toast.makeText(this, "Enter City", Toast.LENGTH_SHORT).show()
                }
                else if (sPincode.isEmpty()) {
                    Toast.makeText(this, "Enter Pincode", Toast.LENGTH_SHORT).show()
                }

                else
                {
                    if (Session.filename!=null)
                        homeViewModel.AddShop(Session.userToken,HomeFragment.unit_token,sName,cPerson,cNumber,selectedShopType,licenseNumber,AwsConstants.S3_URL+"/"+AwsConstants.folderPath+Session.userToken+"/"+Session.filename+"."+Session.fileextension,sAddress,sCity,sPincode,sLat)
                    else
                        homeViewModel.AddShop(Session.userToken,HomeFragment.unit_token,sName,cPerson,cNumber,selectedShopType,licenseNumber,"",sAddress,sCity,sPincode,sLat)
                }
            }catch (e:Exception)
            {
                print(e)
            }
        }

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null){
                if (result.resultCode == AppCompatActivity.RESULT_OK){
                    try {
                        pickiT.getPath(result!!.data!!.data, Build.VERSION.SDK_INT);
                    }catch (e:Exception)
                    {
                        System.out.println(e)
                    }
                }
            }
        }
        resultLauncherOne = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.data != null){
                if (result.resultCode == AppCompatActivity.RESULT_OK){
                    try {
                        pickiT.getPath(result!!.data!!.data, Build.VERSION.SDK_INT);
                    }catch (e:Exception) {
                        System.out.println(e)
                    }
                }
            }
        }
    }

    private val ShopTypeResponse = Observer<ShopTypeData> {
        System.out.println(it)
        spinner= ArrayList()
        spinner?.addAll(it.data!!)
        print(spinner)

        val spinnerAdapter = SpinnerAdapter(context = this, spinnerData = spinner!!, itemClicker = ::onDropDownSelected)
        addnewshopBinding.spinner.adapter = spinnerAdapter
        addnewshopBinding.shopType.setOnClickListener { addnewshopBinding.spinner.performClick() }
    }
    private val AddShopResponse = Observer<ShopTypeData> {
        System.out.println(it)
        if (it.status_code == 200)
        {
            HomeFragment.isPlaceOrder = true
            Session.shopId = 0
            AddNewOrder()
        }
        else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    private fun onDropDownSelected(data: ShopTypeData.ShopListData) {
        addnewshopBinding.shopType.text = data.shop_type_name
        selectedShopType = data.shop_code
        hideSpinnerDropDown(addnewshopBinding.spinner)
    }
    private fun hideSpinnerDropDown(spinner: Spinner?) {
        try {
            val method: Method = Spinner::class.java.getDeclaredMethod("onDetachedFromWindow")
            method.isAccessible = true
            method.invoke(spinner)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    private fun Readpermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_PERMISSION
            )
            ActivityCompat.requestPermissions(
                this as Activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION
            )
        }
        else
        {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            resultLauncher.launch(intent)
        }

    }

    override fun PickiTonUriReturned() {
        TODO("Not yet implemented")
    }

    override fun PickiTonStartListener() {
        TODO("Not yet implemented")
    }

    override fun PickiTonProgressUpdate(progress: Int) {
        TODO("Not yet implemented")
    }

    override fun PickiTonCompleteListener(
        path: String?,
        wasDriveFile: Boolean,
        wasUnknownProvider: Boolean,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        val myFile = File(path)
        fileName=myFile.name
        addnewshopBinding.informatiom.text= fileName
        Session.fileextension=myFile.extension
        val sdf:String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        Session.filename=sdf
        selectedUrl=AwsConstants.S3_URL+"/"+AwsConstants.folderPath+Session.userToken+"/"+Session.filename+"."+Session.fileextension
        val fileUploader = AWSFileUploader(this@AddNewShopActivity, myFile, this)
        fileUploader.uploadImage(AwsConstants.folderPath+Session.userToken)
    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {

    }

    @SuppressLint("SetTextI18n")
    private fun AddNewOrder()
    {
        addNewShopOrderDialog.show()
        addNewShopOrderDialog.findViewById<ImageButton>(R.id.closeRequestSuccess)?.setOnClickListener {
            addNewShopOrderDialog.dismiss()
            finish()
           startActivity(Intent(this@AddNewShopActivity,BottomNavigationActivity::class.java))
        }
        addNewShopOrderDialog.findViewById<AppCompatTextView>(R.id.takeOrdersTextView)?.text="Shop Added Successfully"

        addNewShopOrderDialog.findViewById<MaterialButton>(R.id.collectAmount)?.setOnClickListener {
            addNewShopOrderDialog.dismiss()
            finish()
            startActivity(Intent(this@AddNewShopActivity,BottomNavigationActivity::class.java))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun ChooseUploadDialog()
    {
        chooseUploadDialog.show()
        chooseUploadDialog.findViewById<ImageButton>(R.id.closeRequestSuccess)?.setOnClickListener {
            chooseUploadDialog.dismiss()
            finish()
        }
        chooseUploadDialog.findViewById<MaterialButton>(R.id.imagePicker)?.setOnClickListener {
            chooseUploadDialog.dismiss()
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "image/*"
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            resultLauncher.launch(intent)
        }
        chooseUploadDialog.findViewById<MaterialButton>(R.id.pdfPicker)?.setOnClickListener {
            chooseUploadDialog.dismiss()
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                type = "application/pdf"
                addCategory(Intent.CATEGORY_OPENABLE)
                flags = flags or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            resultLauncherOne.launch(intent)
        }
    }


    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )== PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSION)
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION ) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText( this, "Permission Granted", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun getLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled())
            {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermission()
                    return
                }
                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    if ( task.result == null) {
                        Toast.makeText(this, "We can't find your exact location", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        lastLocation = task.result
                        addnewshopBinding.coordinates.text=String.format("%.5f", lastLocation!!.latitude)+","+String.format("%.5f", lastLocation!!.longitude)
                    }
                }
            }
            else
            {
                Toast.makeText(this, "Turn on Location", Toast.LENGTH_SHORT).show()
                val intent=Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }
        else
        {
            requestPermission()
        }
    }
    private fun showMessage(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    @SuppressLint("SimpleDateFormat")
    fun getRandomNumberString()
    {
        val sdf:String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        Session.filename =sdf

    }

    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

    }

    override fun onSuccess(data: List<String>) {
        System.out.println(data)
    }

    override fun onFailure(exception: Exception) {
        System.out.println(exception)
    }

}