package com.powersoaps.distributorsales.ui.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.powersoaps.distributorsales.R

object DialogUtils {
    fun loadingDialog(context: Context): AlertDialog {
        return AlertDialog.Builder(context, R.style.MaterialAlertDialog)
            .setView(R.layout.dialog_loading)
            .setCancelable(false)
            .create()
    }
}