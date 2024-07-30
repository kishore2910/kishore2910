package com.powersoaps.distributorsales.ui.main.activity.summary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import com.powersoaps.distributorsales.databinding.ActivitySummaryBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity

class SummaryActivity : BaseActivity() {

    private val summaryBinding by lazy { ActivitySummaryBinding.inflate(layoutInflater) }

    val tabList = arrayOf("Day Summary", "SKU Summary")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(summaryBinding.root)
        viewpager_method()
        summaryBinding.back.setOnClickListener {
            onBackPressed()
        }
    }
    fun viewpager_method()
    {
        val viewPager = summaryBinding.viewPager
        val tabLayout = summaryBinding.tablayout

        val adapter = SummaryViewPager(this@SummaryActivity.supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabList[position].toString()
        }.attach()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}