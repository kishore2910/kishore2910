package com.powersoaps.distributorsales.ui.main.activity.introscreens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.LoginData
import com.powersoaps.distributorsales.data.model.UserData
import com.powersoaps.distributorsales.data.pref.Preference
import com.powersoaps.distributorsales.databinding.ActivityLoginBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.Util
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel

class LoginActivity : BaseActivity() {

    private val loginScreen by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    private val authViewModel: AuthViewModel by viewModels()


    companion object
    {
        lateinit var mobilenum: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(loginScreen.root)



        // Initially, set the input type to password
        loginScreen.passwordLayout.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        loginScreen.eye.setImageResource(R.drawable.baseline_visibility_off_24)
// Set an OnClickListener for the eye icon to toggle password visibility
        loginScreen.eye.setOnClickListener {
            val cursorPosition = loginScreen.passwordLayout.selectionStart // Save cursor position
            if (loginScreen.passwordLayout.transformationMethod == PasswordTransformationMethod.getInstance()) {
                // Switch to visible password
                loginScreen.passwordLayout.transformationMethod = HideReturnsTransformationMethod.getInstance()
                loginScreen.eye.setImageResource(R.drawable.password_eye)
            } else {
                // Switch to hidden password
                loginScreen.passwordLayout.transformationMethod = PasswordTransformationMethod.getInstance()
                loginScreen.eye.setImageResource(R.drawable.baseline_visibility_off_24)
            }
            // Restore cursor position
            loginScreen.passwordLayout.setSelection(cursorPosition)
        }


        authViewModel.loginNewData.observe(this@LoginActivity, userLoginResponse)
        loginScreen.loginmobile.setOnClickListener {
            val mobilefield = loginScreen.mobilenumber.text.toString().trim()
            val password = loginScreen.passwordLayout.text.toString()
            if (mobilefield.isEmpty() ) {
                Toast.makeText(this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show()
            }
            else if (mobilefield.length!=10) {
                Toast.makeText(this, "Enter 10 digit Mobile Number", Toast.LENGTH_SHORT).show()
            }
            else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter valid Password", Toast.LENGTH_SHORT).show()
            }
            else
            {
                if (isNetworkConnected(this))
                {

                    loadingDialog(true)

                    FirebaseMessaging.getInstance().token.addOnCompleteListener { it ->
                        if (it.isComplete) {
//                            Log.d("TAG", "FirebaseMessaging:${it.result} ")
                            try {
                                Log.d("TAG", "FirebaseMessaging:${it.result} ")
                                Session.fireBaseToken = it.result.toString()
                            }catch (e:Exception){
                                Log.d("TAG", "FirebaseMessaging:${Session.fireBaseToken} ")
                            }

//                            Handler(Looper.getMainLooper()).postDelayed({  data() }, 1000)
                        }
                    }

                    val jsonObject = JsonObject().apply {
                        addProperty("phone_number",mobilefield)
                        addProperty("password",password )
                        addProperty("device_token",Session.fireBaseToken)
                    }
                    authViewModel.loginNew(jsonObject)
                }
                else
                    startActivity(Intent(this, NoInternetActivity::class.java))
            }
        }

        loginScreen.forgetPassword.setOnClickListener {
            val intent = Intent(this, ForgetPassword ::class.java)
            startActivity(intent)
        }
    }
    private fun nextscreen() {
        mobilenum = loginScreen.mobilenumber.text.toString()
        val intent = Intent(this, BottomNavigationActivity::class.java)
        startActivity(intent)
    }
    private val userLoginResponse = Observer<UserData> {
        loadingDialog(false)
        if (it.status_code == 200) {

            Session.userID = it.data!!.id.toString()
            Session.userToken = it.data!!.token.toString()
            Session.userName = it.data!!.name.toString()
            Session.userCode =it.data!!.employees_code.toString()
            Session.userDepartmentToken = it.data!!.deparment_token.toString()
            Session.userGender = it.data!!.gender.toString()
            Session.userMobile = it.data!!.mobile_number.toString()
            Session.userRegion = it.data.region.toString()
            Session.userDepName = it.data!!.dep_name.toString()
            Preference(this).setParentDetails(
                id = Session.userID.toString(),
                token = Session.userToken.toString(),
                name = Session.userName.toString(),
                employeecode = Session.userCode.toString(),
                departmenttoken = Session.userDepartmentToken.toString(),
                gender = Session.userGender.toString(),
                mobile = Session.userMobile.toString(),
                region = Session.userRegion.toString(),
                depname = Session.userDepName.toString()
            )
            nextscreen()
        } else {
//            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            errors("Error", it.message)
        }
    }


}