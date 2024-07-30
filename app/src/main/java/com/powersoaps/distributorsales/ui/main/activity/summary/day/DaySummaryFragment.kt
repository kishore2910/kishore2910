package com.powersoaps.distributorsales.ui.main.activity.summary.day

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.SummaryData
import com.powersoaps.distributorsales.databinding.FragmentDaySummaryBinding
import com.powersoaps.distributorsales.ui.base.BaseFragment
import com.powersoaps.distributorsales.ui.main.activity.bottomnav.ui.HomeFragment
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.adapter.SummaryAdapter
import com.powersoaps.distributorsales.ui.viewmodels.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import com.powersoaps.distributorsales.ui.utils.Session
import java.text.DecimalFormat
import kotlin.math.roundToInt


class DaySummaryFragment : BaseFragment() {


    private val daybinding by lazy { FragmentDaySummaryBinding.inflate(layoutInflater) }


    private val summaryAdapter by lazy { SummaryAdapter(data) }

    private val homeViewModel: HomeViewModel by viewModels()

    var totalProductcount:Int=0

    companion object {
        lateinit var data: ArrayList<SummaryData.DaySummmaryList>
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return daybinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daybinding.name.text=getCurrentDate()
        daybinding.date.text= HomeFragment.todayCoveredCount+"/"+HomeFragment.todayCoveredTotalCount
        homeViewModel.daySummaryData.observe(requireActivity(), daySummaryResponse)
        if (isNetworkConnected(requireActivity())) {
            loadingDialog(true)
            homeViewModel.daySummary(Session.userToken)
        } else
            requireActivity().startActivity(Intent(requireActivity(), NoInternetActivity::class.java))

    }
    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("dd MMM yyyy")
        return sdf.format(Date())
    }
    @SuppressLint("SetTextI18n")
    private val daySummaryResponse = Observer<SummaryData> {
        loadingDialog(false)
        var totalPrice: Double =0.0
        if (it.status_code==200)
        {
            data = ArrayList()
            System.out.println(it)
            for(list in 0 until it.data!!.size)
            {
                data.add(SummaryData.DaySummmaryList(
                    it.data!![list].shop_name,it.data!![list].quantity,
                    it.data!![list].sale_amount,it.data!![list].shop_type
                ))
                totalPrice=totalPrice+(it.data!![list].sale_amount.toDouble())
                totalProductcount=totalProductcount+it.data[list].quantity.toInt()
            }
            if (data.size!=0) {
                daybinding.listitem.adapter = summaryAdapter

            }
            System.out.println(data)
//            System.out.println(totalProductcount)
            daybinding.totalItems.text= data.size.toString()
//            daybinding.totalSales.text= totalProductcount.toString()
//            daybinding.totalSales.text= "₹"+" "+String.format("%.1f", totalPrice?.toDouble())
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = totalPrice //If your num is in String
            val currency = fmt.format(billamount)
            daybinding.totalSales.text= "₹"+" "+currency

        }
    }



}