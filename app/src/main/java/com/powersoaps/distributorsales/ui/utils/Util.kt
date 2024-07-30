package com.powersoaps.distributorsales.ui.utils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object Util {
    var randombannerimage = ArrayList<String>()
    var APP_URL:String =""
    var supportNumber = "0413-132266111"

    fun Context.Alertdialog(message: String,title:String){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setNegativeButton("", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                dialog.dismiss()
            })
            .show()
    }
}