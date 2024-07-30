package com.powersoaps.distributorsales.ui.main.activity.summary.sku

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.SkuSummaryData
import com.powersoaps.distributorsales.databinding.FragmentSkuSummaryBinding
import com.powersoaps.distributorsales.ui.base.BaseFragment
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.activity.summary.day.DaySummaryFragment
import com.powersoaps.distributorsales.ui.main.adapter.SkuSummaryAdapter
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class SkuSummaryFragment : BaseFragment() {

    private val skubinding by lazy { FragmentSkuSummaryBinding.inflate(layoutInflater) }


    private val summaryAdapter by lazy { SkuSummaryAdapter(skudata) }

    private val homeViewModel: HomeViewModel by viewModels()

    companion object
    {
        lateinit var skudata: ArrayList<SkuSummaryData.dataobject.SkuSummaryList>

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return skubinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        skubinding.name.text=getCurrentDate()
        skubinding.date.text= HomeFragment.todayCoveredCount+"/"+ HomeFragment.todayCoveredTotalCount
        homeViewModel.skuSummaryData.observe(requireActivity(), skuSummaryResponse)

        if (isNetworkConnected(requireActivity()))
        {
            loadingDialog(true)
            homeViewModel.skuSummary(Session.userToken)
        }
        else
            requireActivity().startActivity(Intent(requireActivity(), NoInternetActivity::class.java))

    }
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("dd MMM yyyy")
        return sdf.format(Date())
    }
    @SuppressLint("SetTextI18n")
    private val skuSummaryResponse = Observer<SkuSummaryData> {
        loadingDialog(false)
        var totalPrice: Double =0.0
        var product_count: String =""
        if (it.status_code==200)
        {
            skudata = ArrayList()
            System.out.println(it)
            for(list in 0 until it.data!!.skulist!!.size)
            {
                skudata.add(SkuSummaryData.dataobject.SkuSummaryList(
                    it.data!!.skulist!![list].product_name,it.data!!.skulist!![list].sales_amt,
                    it.data!!.skulist!![list].box_value,it.data!!.skulist!![list].quantity_value,
                    it.data!!.skulist!![list].quantity_units
                ))
                totalPrice=totalPrice+(it.data!!.skulist!![list].sales_amt.toDouble())
                product_count=it.data!!.productdetails!!.product_count.toString()

            }
            if (skudata.size!=0) skubinding.listitem.adapter = summaryAdapter
            System.out.println(skudata)
            skubinding.totalItems.text= skudata.size.toString()
//            if (product_count!= "null")
//            skubinding.totalSales.text= "₹"+" "+String.format("%.1f", totalPrice?.toDouble())
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = totalPrice //If your num is in String
            val currency = fmt.format(billamount)
            skubinding.totalSales.text= "₹"+" "+currency

        }

    }

}