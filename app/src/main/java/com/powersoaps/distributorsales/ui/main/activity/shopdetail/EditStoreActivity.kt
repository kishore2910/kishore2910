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
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.hbisoft.pickit.PickiT
import com.hbisoft.pickit.PickiTCallbacks
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.ServerResponse
import com.powersoaps.distributorsales.data.model.ShopTypeData
import com.powersoaps.distributorsales.databinding.ActivityEditStoreBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.adapter.SpinnerAdapter
import com.powersoaps.distributorsales.ui.utils.AWSFileUploader
import com.powersoaps.distributorsales.ui.utils.AwsConstants
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.io.File
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*

class EditStoreActivity : BaseActivity(), AWSFileUploader.AWSFileUploadListener, PickiTCallbacks {

    private val editStoreBinding by lazy { ActivityEditStoreBinding.inflate(layoutInflater) }

    lateinit private var resultLauncher: ActivityResultLauncher<Intent>;

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var lastLocation: Location? = null

    private val homeViewModel: HomeViewModel by viewModels()

    private var spinner:ArrayList<ShopTypeData.ShopListData> ? = null

    private val editNewShopOrderDialog by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.dialog_order_complete, null))
        setCancelable(false)} }

    private val chooseUploadDialog by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme).apply { setContentView(layoutInflater.inflate(R.layout.bottom_upload_choose, null))
        setCancelable(false)} }


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var  pickiT: PickiT;
        lateinit var  fileName: String;
        lateinit var  selectedShopType: String;
        private val REQUEST_PERMISSION = 100
        lateinit var  sname: String;
        lateinit var  personname: String;
        lateinit var  personmobile: String;
        lateinit var  shoptype: String;
        lateinit var  licensenumber: String;
        lateinit var  licenseImage: String;
        lateinit var  address: String;
        lateinit var  city: String;
        lateinit var  pincode: String;
        lateinit var  coordinates: String;
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(editStoreBinding.root)
        homeViewModel.EditShopData.observe(this@EditStoreActivity,EditShopResponse)
        homeViewModel.ShopTypeData.observe(this@EditStoreActivity,ShopTypeResponse)
        pickiT = PickiT(this, this, this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val bundle = intent.extras
        if (bundle != null) {
            sname = bundle.getString("sname").toString()
            personname = bundle.getString("personname").toString()
            personmobile = bundle.getString("personmobile").toString()
            shoptype = bundle.getString("shoptype").toString()
            licensenumber = bundle.getString("licensenumber").toString()
            licenseImage = bundle.getString("licenseImage").toString()
            address = bundle.getString("address").toString()
            city = bundle.getString("city").toString()
            pincode = bundle.getString("pincode").toString()
            coordinates = bundle.getString("coordinates").toString()
            editStoreBinding.shopName.setText(sname)
            editStoreBinding.contactPerson.setText(personname)
            editStoreBinding.contactNumber.setText(personmobile)
            editStoreBinding.licenseNumber.setText(licensenumber)
            editStoreBinding.informatiom.setText(licenseImage)
            selectedShopType=bundle.getString("shoptoken").toString()
            editStoreBinding.shopType.setText(shoptype)
            editStoreBinding.address.setText(address)
            editStoreBinding.city.setText(city)
            editStoreBinding.pincode.setText(pincode)
            editStoreBinding.coordinates.setText(coordinates)
        }

        if (isNetworkConnected(this))
        {
            loadingDialog(true)
            homeViewModel.ShopTypeApi()
        }
        else startActivity(Intent(this, NoInternetActivity::class.java))

        editStoreBinding.back.setOnDebounceListener {
            onBackPressed()
        }
        editStoreBinding.cancelShop.setOnDebounceListener {
            onBackPressed()
        }

        editStoreBinding.uploadImage.setOnDebounceListener {
            Readpermissions()
        }
        editStoreBinding.coordinates.setOnDebounceListener {
            getLocation()
        }
        editStoreBinding.saveShop.setOnDebounceListener {
            lateinit var sLat:String
            val sName=editStoreBinding.shopName.text.toString()
            val cPerson=editStoreBinding.contactPerson.text.toString()
            val cNumber=editStoreBinding.contactNumber.text.toString()
            val sType=editStoreBinding.shopType.text.toString()
            val licenseNumber=editStoreBinding.licenseNumber.text.toString()
            val sAddress=editStoreBinding.address.text.toString()
            val sCity=editStoreBinding.city.text.toString()
            val sPincode=editStoreBinding.pincode.text.toString()
            if (lastLocation!=null) sLat=(lastLocation)!!.latitude.toString()+","+(lastLocation)!!.longitude.toString()
            else sLat=editStoreBinding.coordinates.text.toString()
            if (sName.isEmpty())
            {
                Toast.makeText(this, "Enter Shop Name", Toast.LENGTH_SHORT).show()
            }
//            else if (cPerson.isEmpty())
//            {
//                Toast.makeText(this, "Enter Contact Person Name", Toast.LENGTH_SHORT).show()
//            }
            else if (cNumber.isEmpty())
            {
                Toast.makeText(this, "Enter Contact Person Number", Toast.LENGTH_SHORT).show()
            }
            else if (cNumber.length<10)
            {
                Toast.makeText(this, "Enter 10 Digit Contact Number", Toast.LENGTH_SHORT).show()
            }
            else if (sType=="Shop Type")
            {
                Toast.makeText(this, "Enter Shop Type", Toast.LENGTH_SHORT).show()
            }
//            else if (licenseNumber.isEmpty())
//            {
//                Toast.makeText(this, "Enter License Number", Toast.LENGTH_SHORT).show()
//            }
            else if (sAddress.isEmpty())
            {
                Toast.makeText(this, "Enter Address", Toast.LENGTH_SHORT).show()
            }
            else if (sCity.isEmpty())
            {
                Toast.makeText(this, "Enter City", Toast.LENGTH_SHORT).show()
            }
            else if (sPincode.isEmpty())
            {
                Toast.makeText(this, "Enter Pincode", Toast.LENGTH_SHORT).show()
            }
//            else if (editStoreBinding.coordinates.text.toString().isEmpty())
//            {
//                Toast.makeText(this, "Select Location", Toast.LENGTH_SHORT).show()
//            }
            else
            {
                if (isNetworkConnected(this))
                {
                    loadingDialog(true)
                    if (Session.filename!=null)
                        homeViewModel.EditShop(Session.userToken,HomeFragment.selectedShopId,HomeFragment.unit_token,sName,cPerson,cNumber,selectedShopType,licenseNumber,AwsConstants.S3_URL+"/"+AwsConstants.folderPath+Session.userToken+"/"+Session.filename+"."+Session.fileextension,sAddress,sCity,sPincode,sLat)
                    else
                        homeViewModel.EditShop(Session.userToken,HomeFragment.selectedShopId,HomeFragment.unit_token,sName,cPerson,cNumber,selectedShopType,licenseNumber,"",sAddress,sCity,sPincode,sLat)
//                    homeViewModel.EditShop(
//                        Session.userToken,HomeFragment.selectedShopId,
//                        HomeFragment.unit_token,sName,cPerson,cNumber,
//                        selectedShopType,licenseNumber,licenseImage,sAddress,sCity,sPincode,sLat)
                }
                else startActivity(Intent(this, NoInternetActivity::class.java))
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
    }

    override fun onSuccess(data: List<String>) {
        System.out.println(data)
    }

    override fun onFailure(exception: Exception) {
        System.out.println(exception)
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
        fileName =myFile.name
        editStoreBinding.informatiom.text= fileName
        Session.fileextension=myFile.extension
        val sdf:String = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        Session.filename=sdf
        licenseImage=AwsConstants.S3_URL+"/"+AwsConstants.folderPath+Session.userToken+"/"+Session.filename+"."+Session.fileextension
        val fileUploader = AWSFileUploader(this, myFile, this)
        fileUploader.uploadImage(AwsConstants.folderPath+Session.userToken)
    }

    override fun PickiTonMultipleCompleteListener(
        paths: ArrayList<String>?,
        wasSuccessful: Boolean,
        Reason: String?
    ) {
        TODO("Not yet implemented")
    }
    private fun Readpermissions()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
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
            resultLauncher.launch(intent)
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
                        Toast.makeText(this, "Null Recieved", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        lastLocation = task.result
                        editStoreBinding.coordinates.text=String.format("%.5f", lastLocation!!.latitude)+","+String.format("%.5f", lastLocation!!.longitude)
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
    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION )== PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        return false
    }

    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)

    }
    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION),
            REQUEST_PERMISSION
        )
    }
    private val EditShopResponse = Observer<ServerResponse> {
        loadingDialog(false)
        System.out.println(it)
        if (it.status_code == 200)
        {
            EditOrder()
        }
        else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }
    private val ShopTypeResponse = Observer<ShopTypeData> {
        loadingDialog(false)
        System.out.println(it)
        spinner= ArrayList()
        spinner?.addAll(it.data!!)
        print(spinner)
        val spinnerAdapter = SpinnerAdapter(context = this, spinnerData = spinner!!, itemClicker = ::onDropDownSelected)
        editStoreBinding.spinner.adapter = spinnerAdapter
        editStoreBinding.shopType.setOnClickListener { editStoreBinding.spinner.performClick() }
    }
    private fun onDropDownSelected(data: ShopTypeData.ShopListData) {
        editStoreBinding.shopType.text=data.shop_type_name
        selectedShopType =data.shop_code
        hideSpinnerDropDown(editStoreBinding.spinner)
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
    @SuppressLint("SetTextI18n")
    private fun EditOrder() {
        editNewShopOrderDialog.show()
        editNewShopOrderDialog.findViewById<ImageButton>(R.id.closeRequestSuccess)?.setOnClickListener {
            editNewShopOrderDialog.dismiss()
            finish()
        }
        editNewShopOrderDialog.findViewById<AppCompatTextView>(R.id.takeOrdersTextView)?.text= "Shop Edited Successfully"

        editNewShopOrderDialog.findViewById<MaterialButton>(R.id.collectAmount)?.setOnClickListener {
            editNewShopOrderDialog.dismiss()
            finish()
        }
    }
}