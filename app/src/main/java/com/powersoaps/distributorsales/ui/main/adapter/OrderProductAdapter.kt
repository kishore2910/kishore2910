package com.powersoaps.distributorsales.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.OrderDetailsData
import com.powersoaps.distributorsales.data.model.SummaryData
import com.powersoaps.distributorsales.databinding.OrderProductAdapterBinding
import java.text.DecimalFormat

class OrderProductAdapter (var loadlist: ArrayList<OrderDetailsData.OrderProductList.OrderProductData>) :
    RecyclerView.Adapter<OrderProductAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val loadingSheetAdapter = OrderProductAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
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
    inner class MyViewHolder(private val loadingSheetAdapter: OrderProductAdapterBinding) : RecyclerView.ViewHolder(loadingSheetAdapter.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(task: OrderDetailsData.OrderProductList.OrderProductData) {
//            val context = beatAdapter.root.context
            //bind the item here
            loadingSheetAdapter.name.text = task.product_name
            loadingSheetAdapter.qty.text  = task.quantity+" "+task.units
            val fmtgst = DecimalFormat("#,##,###.##")
            val billamountgst: Double = task.amount!!.toDouble() //If your num is in String
            val currencygst = fmtgst.format(billamountgst)
            loadingSheetAdapter.amount.text  =loadingSheetAdapter.root.context.getString(R.string.rupee)+" "+ currencygst
            if (task.is_discount_enable == "0"){
                loadingSheetAdapter.discounted.visibility = View.GONE
            }else if (task.is_discount_enable == "1"){
                loadingSheetAdapter.discounted.visibility = View.VISIBLE
                loadingSheetAdapter.discounted.text = "("+task.percentage_value+"% Discounted)"

            }
            if (task.is_free==true)
            {
                loadingSheetAdapter.free.visibility = View.VISIBLE
                loadingSheetAdapter.amount.visibility = View.GONE
            }
            else
            {
                loadingSheetAdapter.free.visibility = View.GONE
                loadingSheetAdapter.amount.visibility = View.VISIBLE
            }
        }

    }

}