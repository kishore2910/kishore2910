package com.powersoaps.distributorsales.ui.main.activity.introscreens

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.LoginData
import com.powersoaps.distributorsales.data.model.OtpData
import com.powersoaps.distributorsales.data.model.UserData
import com.powersoaps.distributorsales.data.pref.Preference
import com.powersoaps.distributorsales.databinding.ActivityOtpBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.BottomNavigationActivity
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel
import java.util.concurrent.TimeUnit

class OtpActivity : BaseActivity(), View.OnClickListener, TextWatcher {

    private val otpScreen by lazy { ActivityOtpBinding.inflate(layoutInflater) }

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(otpScreen.root)
        authViewModel.otpData.observe(this@OtpActivity, otpResponse)
        authViewModel.userData.observe(this@OtpActivity, userLoginResponse)

        initListener()

        otpScreen.verify.setOnClickListener {
            val otpNumber = otpScreen.otp1.text.toString().trim()+otpScreen.otp2.text.toString().trim()+
                    otpScreen.otp3.text.toString().trim()+otpScreen.otp4.text.toString().trim()
            if (otpNumber.length==4)
            {
                if (isNetworkConnected(this))
                {
                    loadingDialog(true)
                    authViewModel.otp(ForgetPassword.mobileNumber,otpNumber)
                }
                else
                    startActivity(Intent(this, NoInternetActivity::class.java))
            }
            else{
                Toast.makeText(this, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
            }
//
//            if (otpScreen.verify.text.toString().equals("Verify OTP")){
//                if (otpNumber.length==4)
//                {
//                    if (isNetworkConnected(this))
//                    {
//                        loadingDialog(true)
//                        authViewModel.otp(ForgetPassword.mobileNumber,otpNumber)
//                    }
//                    else
//                        startActivity(Intent(this, NoInternetActivity::class.java))
//                }
//                else{
//                    Toast.makeText(this, "Please Enter Valid OTP", Toast.LENGTH_SHORT).show()
//                }
//            }
//            else if (otpScreen.verify.text.toString().equals("Resend Code")){
//                otpScreen.verify.setTextColor(ContextCompat.getColor(this, R.color.white));
//                otpScreen.verify.setTextColor(ContextCompat.getColor(this, R.color.white));
//                otpScreen.otpview.requestFocus()
//                otpScreen.verify.text = "Verify OTP"
//                loadingDialog(true)
//                authViewModel.login(ForgetPassword.mobileNumber)
//            }


        }


        otpScreen.Timer.setOnDebounceListener {
            if (otpScreen.Timer.text.toString().equals("Resend Code")) {
                otpScreen.verify.setTextColor(ContextCompat.getColor(this, R.color.white));
                otpScreen.verify.setTextColor(ContextCompat.getColor(this, R.color.white));
                otpScreen.otpview.requestFocus()
                if (isNetworkConnected(this))
                {
                    loadingDialog(true)
                    authViewModel.login(ForgetPassword.mobileNumber)
                }
                else startActivity(Intent(this, NoInternetActivity::class.java))
            }

        }
    }
    private fun nextscreen() {
        val intent = Intent(this, CreatePassword::class.java)
        startActivity(intent)
    }

    private fun initListener() {
        otpScreen.verify.setOnClickListener(this)
        otpScreen.otp1.addTextChangedListener(this)
        otpScreen.otp2.addTextChangedListener(this)
        otpScreen.otp3.addTextChangedListener(this)
        otpScreen.otp4.addTextChangedListener(this)
        otpScreen.otp1.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                otpScreen.otp1.text?.clear()
            }
            false
        }
        otpScreen.otp2.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                otpScreen.otp2.text?.clear()
                otpScreen.otp1.requestFocus()
                otpScreen.otp1.isCursorVisible = true
            }
            false
        }
        otpScreen.otp3.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                otpScreen.otp3.text?.clear()
                otpScreen.otp2.requestFocus()
                otpScreen.otp2.isCursorVisible = true
            }
            false
        }
        otpScreen.otp4.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_DEL) {
                otpScreen.otp4.text?.clear()
                otpScreen.otp3.requestFocus()
                otpScreen.otp3.isCursorVisible = true
            }
            false
        }
        otpScreen.otp1.requestFocus()
        starttimer()
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        when {
            otpScreen.otp1.isFocused -> {
                if (p0.isNullOrEmpty()) {
                    otpScreen.otp1.text?.clear()
                    otpScreen.otp1.requestFocus()
                    otpScreen.otp1.isCursorVisible = true
                } else {
                    otpScreen.otp2.requestFocus()
                    otpScreen.otp2.isCursorVisible = true
                }
            }
            otpScreen.otp2.isFocused -> {
                if (p0.isNullOrEmpty()) {
                    otpScreen.otp2.text?.clear()
                    otpScreen.otp1.requestFocus()
                    otpScreen.otp1.isCursorVisible = true
                } else {
                    otpScreen.otp3.requestFocus()
                    otpScreen.otp3.isCursorVisible = true
                }
            }
            otpScreen.otp3.isFocused -> {
                if (p0.isNullOrEmpty()) {
                    otpScreen.otp3.text?.clear()
                    otpScreen.otp2.requestFocus()
                    otpScreen.otp2.isCursorVisible = true
                } else {
                    otpScreen.otp4.requestFocus()
                    otpScreen.otp4.isCursorVisible = true
                }
            }
            otpScreen.otp4.isFocused -> {
                if (p0.isNullOrEmpty()) {
                    otpScreen.otp4.text?.clear()
                    otpScreen.otp3.requestFocus()
                    otpScreen.otp3.isCursorVisible = true
                } else {
                    otpScreen.otp4.requestFocus()
                    otpScreen.otp4.isCursorVisible = true
                }
            }
        }

    }

    override fun afterTextChanged(p0: Editable?) {

    }
    private val otpResponse = Observer<OtpData> {
        loadingDialog(false)
        if (it.statusCode == 200) {

            nextscreen()

        } else {
            if (it.message=="OTP not verified")
                Toast.makeText(this, "Invalid OTP", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(this,it.message, Toast.LENGTH_SHORT).show()

        }
    }

    private fun starttimer() {
        val timer = object : CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                otpScreen.Timer.text = (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60).toString() + " " + getString(R.string.sec)
            }
            @SuppressLint("SetTextI18n")
            override fun onFinish() {
//                otpScreen.Timer.text = "Time elapsed"
                otpScreen.Timer.text = "Resend Code"
                otpScreen.otp1.text = null
                otpScreen.otp2.text = null
                otpScreen.otp3.text = null
                otpScreen.otp4.text = null
//                otpScreen.Timer.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.timer_icon,0, 0, 0)
                otpScreen. verify.setBackgroundColor(ContextCompat.getColor(this@OtpActivity, R.color.buttoncolor))
                otpScreen.verify.setTextColor(ContextCompat.getColor(this@OtpActivity, R.color.white));
            }
        }
        timer.start()
    }
    private val userLoginResponse = Observer<LoginData> {
        loadingDialog(false)
        if (it.statusCode == 200) {
            starttimer()
        } else {
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }

}