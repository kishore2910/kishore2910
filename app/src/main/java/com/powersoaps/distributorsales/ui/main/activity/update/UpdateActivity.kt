package com.powersoaps.distributorsales.ui.main.activity.update

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.powersoaps.distributorsales.databinding.ActivityUpdateBinding
import com.powersoaps.distributorsales.ui.utils.Util
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener

class UpdateActivity : AppCompatActivity() {
    private val updateActivity by lazy { ActivityUpdateBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(updateActivity.root)
        updateActivity.UpdateApp.setOnDebounceListener {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_BROWSABLE)
            intent.data = Uri.parse(Util.APP_URL)
            startActivity(intent)
        }
    }
}