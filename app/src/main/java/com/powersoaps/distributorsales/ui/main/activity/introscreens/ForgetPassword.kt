package com.powersoaps.distributorsales.ui.main.activity.introscreens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.data.model.ForgetPasswordData
import com.powersoaps.distributorsales.data.model.UserData
import com.powersoaps.distributorsales.data.pref.Preference
import com.powersoaps.distributorsales.databinding.ActivityForgetPasswordBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel

class ForgetPassword : BaseActivity() {

    private val forgetBinding by lazy { ActivityForgetPasswordBinding.inflate(layoutInflater) }
    private val authViewModel : AuthViewModel by viewModels()

    companion object{
        lateinit var mobileNumber:String

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(forgetBinding.root)

        authViewModel.forgetPasswordResponse.observe(this@ForgetPassword, forgetPasswordResponse)

        forgetBinding.getOpt.setOnClickListener {
            mobileNumber =  forgetBinding.mobilenumber.text.toString()
            if (mobileNumber.isEmpty()){
                Toast.makeText(this,"Please Enter Register Mobile Number", Toast.LENGTH_SHORT).show()
            }else if (mobileNumber.length != 10){
                Toast.makeText(this,"Enter 10 digit Mobile Number", Toast.LENGTH_SHORT).show()
            }else{
                if (isNetworkConnected(this)){
                    val jsonObject = JsonObject().apply {
                        addProperty("phone_number",mobileNumber)
                    }
                    authViewModel.forgetPassword(jsonObject)
                }else{
                    Toast.makeText(this,"Please turn on ur internet", Toast.LENGTH_SHORT).show()
                }

            }

        }


    }

    private val forgetPasswordResponse = Observer<ForgetPasswordData> {
        loadingDialog(false)
        if (it.statusCode == 200) {

            val intent = Intent(this, OtpActivity::class.java)
            startActivity(intent)

        } else {
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            errors("Error", it.message)
        }
    }

}