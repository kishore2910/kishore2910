package com.powersoaps.distributorsales.ui.main.activity.introscreens

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.data.model.CreatePasswordData
import com.powersoaps.distributorsales.databinding.ActivityCreatePasswordBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel

class CreatePassword : BaseActivity() {

    private val createBinding by lazy { ActivityCreatePasswordBinding.inflate(layoutInflater) }
    private val authViewModel : AuthViewModel by viewModels()

    var enterPassword = ""
    var createPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(createBinding.root)

        authViewModel.createPasswordData.observe(this@CreatePassword, createPasswordResponse)

        enterPassword =  createBinding.enterPassword.text.toString()
        createPassword = createBinding.createPassword.text.toString()

        createBinding.passwordBtn.setOnClickListener {
            enterPassword =  createBinding.enterPassword.text.toString()
            createPassword = createBinding.createPassword.text.toString()

            if(enterPassword.isEmpty()){
                Toast.makeText(this, "Please enter new password", Toast.LENGTH_SHORT).show()

            }else if(enterPassword.length < 6){
                Toast.makeText(this, "Please enter minimum 6 digits", Toast.LENGTH_SHORT).show()

            }else if(createPassword.isEmpty()){
                Toast.makeText(this, "Please enter confirm password", Toast.LENGTH_SHORT).show()

            }else {
                if (enterPassword.isNotEmpty() && createPassword.isNotEmpty()){
                    if (enterPassword == createPassword ){
                        if (isNetworkConnected(this)){
                            val jsonObject = JsonObject().apply {
                                addProperty("phone_number",ForgetPassword.mobileNumber)
                                addProperty("password",createPassword)

                            }
                            authViewModel.passwordUpdate(jsonObject)
                        }else {
                            Toast.makeText(this,"Please turn on ur internet", Toast.LENGTH_SHORT).show()
                        }
                    }else {
                        Toast.makeText(this, "Your confirmation password does not match the new password", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    Toast.makeText(this, "Please Create password", Toast.LENGTH_SHORT).show()

                }
            }


        }



    }

    private val createPasswordResponse = Observer<CreatePasswordData>{
        if (it.statusCode == 200){
            Toast.makeText(this, "Your password has been updated", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }else{
            it.message?.let { it1 -> errors("Error", it1) }
        }
    }
}