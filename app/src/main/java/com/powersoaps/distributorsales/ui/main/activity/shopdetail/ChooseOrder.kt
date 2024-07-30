package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.powersoaps.distributorsales.data.model.OrderHistoryData
import com.powersoaps.distributorsales.databinding.ActivityChooseOrderBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener

class ChooseOrder : BaseActivity() {

    val historydata = ArrayList<OrderHistoryData.OrderHistoryObjects.OrderHistoryList>()

    var shopname: String = ""

    private val chooseOrder by lazy { ActivityChooseOrderBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(chooseOrder.root)
        val bundle = intent.extras
        if (bundle != null) {
            shopname = bundle.getString("shopname").toString()
        }
        chooseOrder.ordername.text = shopname
        chooseOrder.back.setOnDebounceListener {
            onBackPressed()
        }
    }
}