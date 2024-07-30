package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.powersoaps.distributorsales.databinding.ActivitySalesReturnBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener

class SalesReturn : BaseActivity() {

    var shopname = ""
    private val salesReturn by lazy { ActivitySalesReturnBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val bundle = intent.extras
        if (bundle != null) {
            shopname = bundle.getString("shopname").toString()
        }

        salesReturn.shopname.setText(shopname)
        setContentView(salesReturn.root)

        salesReturn.back.setOnDebounceListener {
            onBackPressed()
        }
        salesReturn.fabbutton.setOnDebounceListener {
            startActivity(Intent(this, ChooseOrder::class.java).putExtra("shopname",shopname))
        }
    }
}