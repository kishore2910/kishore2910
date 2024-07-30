package com.powersoaps.distributorsales.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.SummaryData
import com.powersoaps.distributorsales.databinding.SummaryAdapterBinding
import java.text.DecimalFormat

class SummaryAdapter (var loadlist: ArrayList<SummaryData.DaySummmaryList>) :
    RecyclerView.Adapter<SummaryAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val loadingSheetAdapter = com.powersoaps.distributorsales.databinding.SummaryAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(loadingSheetAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return loadlist.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(loadlist[position])
    }

    //the class is holding the list view
    inner class MyViewHolder(private val loadingSheetAdapter: SummaryAdapterBinding) : RecyclerView.ViewHolder(loadingSheetAdapter.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(task: SummaryData.DaySummmaryList) {
//            val context = beatAdapter.root.context
            //bind the item here
            loadingSheetAdapter.name.text = task.shop_name
            loadingSheetAdapter.qty.text  = task.quantity
            loadingSheetAdapter.shopType.text  = task.shop_type
            val fmttwo = DecimalFormat("#,##,###.##")
            val billamounttwo: Double = task.sale_amount.toDouble() //If your num is in String
            val currencytwo = fmttwo.format(billamounttwo)
            loadingSheetAdapter.amount.text  = loadingSheetAdapter.root.context.getString(R.string.rupee)+" "+currencytwo
        }

    }

}