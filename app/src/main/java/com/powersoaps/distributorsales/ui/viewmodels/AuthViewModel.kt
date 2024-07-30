package com.powersoaps.distributorsales.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.data.AuthRepository

class AuthViewModel : ViewModel() {

    private val authRepository by lazy { AuthRepository() }

    val error by lazy { authRepository.errorString }

    val userData by lazy { authRepository.userLogin }

    val otpData by lazy { authRepository.otpVerify }

    val productData by lazy { authRepository.productList }

    val discountData by lazy { authRepository.discountPercentage }

    val LocationData by lazy { authRepository.locationList }


    val supportList by lazy { authRepository.supportList }

    val loginNewData by lazy { authRepository.loginNewList }

    val forgetPasswordResponse by lazy { authRepository.forgetPasswordList}

    val createPasswordData by lazy { authRepository.createPasswordList }


    val shopVisitedData by lazy { authRepository.shopVisitedList }



    fun login(mobileNumber: String){
        authRepository.loginMethod(mobileNumber)
    }
    fun otp(mobileNumber: String,otp:String){
        authRepository.otpMethod(mobileNumber,otp)
    }
    fun productList(employee_id:String){
        authRepository.ProductListMethod(employee_id)
    }
    fun discountPercentage(employeeToken :String){
        authRepository.discountPercentage(employeeToken)
    }


    fun locationList(employee_id:String,lat:Double,longi:Double,shopid:String){
        authRepository.locationlistMethod(employee_id,lat,longi,shopid)
    }

    fun supportRetailer(jsonObject: JsonObject){

        authRepository.supportRetailer(jsonObject)
    }

    fun loginNew(jsonObject: JsonObject){

        authRepository.loginNew(jsonObject)
    }

    fun forgetPassword(jsonObject: JsonObject){

        authRepository.forgetPassword(jsonObject)
    }

    fun passwordUpdate(jsonObject: JsonObject){

        authRepository.passwordUpdate(jsonObject)
    }


    fun shopVisited(jsonObject: JsonObject){

        authRepository.shopVisited(jsonObject)
    }
}