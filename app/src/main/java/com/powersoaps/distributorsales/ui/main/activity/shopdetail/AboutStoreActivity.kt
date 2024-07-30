package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.data.model.AboutStoreData
import com.powersoaps.distributorsales.data.model.OrderHistoryData
import com.powersoaps.distributorsales.data.model.ShopTypeData
import com.powersoaps.distributorsales.databinding.ActivityAboutStoreBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.activity.introscreens.LoginActivity
import com.powersoaps.distributorsales.ui.main.adapter.SpinnerAdapter
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel

class AboutStoreActivity : BaseActivity() {

    private val aboutStoreBinding by lazy { ActivityAboutStoreBinding.inflate(layoutInflater) }

    private val homeViewModel: HomeViewModel by viewModels()
///
    companion object {
        lateinit var addressName:String
        lateinit var city:String
        lateinit var pincode:String
        lateinit var coordinates:String
        lateinit var licenseImage:String
        lateinit var shoptoken:String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(aboutStoreBinding.root)
        homeViewModel.AboutShopData.observe(this@AboutStoreActivity,AboutStoreResponse)

        if (isNetworkConnected(this))
        {
            loadingDialog(true)
            homeViewModel.AboutShop(Session.userToken,HomeFragment.selectedShopId)

        }
        else startActivity(Intent(this, NoInternetActivity::class.java))

        aboutStoreBinding.back.setOnDebounceListener {
            onBackPressed()
        }
        aboutStoreBinding.callButton.setOnDebounceListener {
            AlertCallDialogue()
        }
        aboutStoreBinding.edit.setOnDebounceListener {
            val shopname=aboutStoreBinding.shopName.text.toString()
            val personname=aboutStoreBinding.contactPerson.text.toString()
            val personmobile=aboutStoreBinding.contactNumber.text.toString()
            val shoptype=aboutStoreBinding.shopType.text.toString()
            val licensenumber=aboutStoreBinding.licenseNumber.text.toString()
            startActivity(Intent(this@AboutStoreActivity, EditStoreActivity::class.java)
                .putExtra("sname",shopname).putExtra("personname",personname)
                .putExtra("personmobile",personmobile).putExtra("shoptype",shoptype)
                .putExtra("licensenumber",licensenumber).putExtra("address", addressName)
                .putExtra("city",city).putExtra("pincode", pincode).putExtra("coordinates", coordinates)
                .putExtra("licenseImage",licenseImage).putExtra("shoptoken",shoptoken)
            )
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
    private fun AlertCallDialogue()
    {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Are you sure you need to contact store?")
            .setCancelable(false)
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:"+ aboutStoreBinding.contactNumber.text.toString())
                startActivity(intent)
            })
            .show()
    }
    @SuppressLint("SetTextI18n")
    private val AboutStoreResponse = Observer<AboutStoreData> {
        loadingDialog(false)
        System.out.println(it)
        if (it.status_code == 200) {
            System.out.println(it)
            for (a in 0 until it.data!!.size) {
                aboutStoreBinding.shopName.text=it.data[a].shop_name
                aboutStoreBinding.contactPerson.text=it.data[a].contact_person
                aboutStoreBinding.shopCreated.text="Created on "+it.data[a].join_date
                aboutStoreBinding.contactNumber.text=it.data[a].mobile_number
                aboutStoreBinding.shopType.text=it.data[a].shop_type
                aboutStoreBinding.licenseNumber.text=it.data[a].license_number
                aboutStoreBinding.address.text=it.data[a].address+",\n"+it.data[a].city+",\n"+it.data[a].pincode
                aboutStoreBinding.coordinates.text=it.data[a].coordinates
                addressName=it.data[a].address.toString()
                city=it.data[a].city.toString()
                pincode=it.data[a].pincode.toString()
                coordinates=it.data[a].coordinates.toString()
                licenseImage=it.data[a].license_image.toString()
                shoptoken=it.data[a].shop_token.toString()
                HomeFragment.selectedShopName=it.data[a].shop_name.toString()
                break
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkConnected(this))
        {
            loadingDialog(true)
            homeViewModel.AboutShop(Session.userToken,HomeFragment.selectedShopId)

        }
        else startActivity(Intent(this, NoInternetActivity::class.java))
    }
}