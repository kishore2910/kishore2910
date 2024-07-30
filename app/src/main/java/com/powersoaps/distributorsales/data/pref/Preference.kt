package com.powersoaps.distributorsales.data.pref

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.powersoaps.distributorsales.ui.main.activity.introscreens.SplashActivity

class Preference(context: Context) {
    private val prefName = "sales_app"
    private val sharedPref: SharedPreferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPref.edit()
    private val keyIsLoggedIn: String = "login"
    private val keyID: String = "id"
    private val keyToken: String = "token"
    private val keyName: String = "name"
    private val keyEmployeeCode: String = "employees_code"
    private val keyDepartmentToken: String = "deparment_token"
    private val keyGender: String = "gender"
    private val keyMobileNumber: String = "mobile_number"
    private val keyRegion: String = "region"
    private val keyDepName: String = "dep_name"

    fun setParentDetails(id: String,token: String, name: String, employeecode: String, departmenttoken: String,
                         gender: String,mobile: String, region: String,depname: String){
        editor.putBoolean(keyIsLoggedIn, true)
        editor.putString(keyID, id)
        editor.putString(keyToken, token)
        editor.putString(keyName, name)
        editor.putString(keyEmployeeCode, employeecode)
        editor.putString(keyDepartmentToken, departmenttoken)
        editor.putString(keyGender, gender)
        editor.putString(keyMobileNumber, mobile)
        editor.putString(keyRegion, region)
        editor.putString(keyDepName, depname)
        editor.commit()
    }

    fun getParentDetails(): HashMap<String, String>{
        val user = HashMap<String, String>()
        user[keyID] = sharedPref.getString(keyID, null).toString()
        user[keyToken] = sharedPref.getString(keyToken, null).toString()
        user[keyName] = sharedPref.getString(keyName, null).toString()
        user[keyEmployeeCode] = sharedPref.getString(keyEmployeeCode, null).toString()
        user[keyDepartmentToken] = sharedPref.getString(keyDepartmentToken, null).toString()
        user[keyGender] = sharedPref.getString(keyGender, null).toString()
        user[keyMobileNumber] = sharedPref.getString(keyMobileNumber, null).toString()
        user[keyRegion] = sharedPref.getString(keyRegion, null).toString()
        user[keyDepName] = sharedPref.getString(keyDepName, "").toString()
        return user
    }

    fun isLoggedIn(): Boolean {
        return sharedPref.getBoolean(keyIsLoggedIn, false)
    }

    fun onLogOut(context: Context) {
        editor.putBoolean(keyIsLoggedIn, false)
        editor.putString(keyID, null)
        editor.putString(keyToken, null)
        editor.putString(keyName, null)
        editor.putString(keyEmployeeCode, null)
        editor.putString(keyDepartmentToken, null)
        editor.putString(keyGender, null)
        editor.putString(keyMobileNumber, null)
        editor.putString(keyRegion, null)
        editor.putString(keyDepName, null)
        editor.commit()
        context.startActivity(Intent(context, SplashActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
    }
}