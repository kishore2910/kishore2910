package com.powersoaps.distributorsales.ui.main.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.OrderHistoryData
import com.powersoaps.distributorsales.databinding.OrderhistoryAdapterBinding
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.OrderHistoryActivity
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.reflect.KFunction0

class OrderHistoryAdapter(
    var context: Context,
    var orderlist: ArrayList<OrderHistoryData.OrderHistoryObjects.OrderHistoryList>,
    var onClicked: (OrderHistoryData.OrderHistoryObjects.OrderHistoryList) -> Unit,
    var onHolderClicked: (OrderHistoryData.OrderHistoryObjects.OrderHistoryList) -> Unit,
    var onTokenPass: (OrderHistoryData.OrderHistoryObjects.OrderHistoryList) -> Unit
) :
    RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val orderHistoryAdapter = OrderhistoryAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(orderHistoryAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return orderlist.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(orderlist[position])
    }

    //the class is holding the list view
    inner class MyViewHolder(private val orderHistoryAdapter: OrderhistoryAdapterBinding) : RecyclerView.ViewHolder(orderHistoryAdapter.root) {

        @SuppressLint("SetTextI18n", "SimpleDateFormat")
        fun bindItems(task: OrderHistoryData.OrderHistoryObjects.OrderHistoryList) {
            val context = orderHistoryAdapter.root.context
            //bind the item here
            orderHistoryAdapter.currentdate.text  = task.schedule_date
//            orderHistoryAdapter.currentdate.text = getCurrentDate()
            orderHistoryAdapter.ordercount.text  = task.order_token
            orderHistoryAdapter.itemcount.text  = task.items
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = task.billing_amount!!.toDouble() //If your num is in String
            val currency = fmt.format(billamount)
            val billamountone: Double = task.paid_amount!!.toDouble() //If your num is in String
            val currencyone = fmt.format(billamountone)
            val billamounttwo: Double = task.outstanding_amt!!.toDouble() //If your num is in String
            val currencytwo = fmt.format(billamounttwo)
            orderHistoryAdapter.amount.text  = context.getString(R.string.rupee)+" "+ currency
            orderHistoryAdapter.paid.text  = context.getString(R.string.rupee)+" "+currencyone
            orderHistoryAdapter.outstanding.text  = context.getString(R.string.rupee)+" "+currencytwo

            orderHistoryAdapter.top.setOnClickListener {
                onHolderClicked(task)
            }

            orderHistoryAdapter.card.setOnClickListener {
                onClicked(task)
            }

            when (task.status) {

                "Pending" -> {
                    orderHistoryAdapter.editBtn.visibility = View.VISIBLE
                }
                "Completed" -> {
                    orderHistoryAdapter.editBtn.visibility = View.INVISIBLE
                }

                "Cancelled" -> {
                    orderHistoryAdapter.editBtn.visibility = View.INVISIBLE
                }

            }

//            if (orderHistoryAdapter.currentdate.text == getCurrentDate()){
//                orderHistoryAdapter.editBtn.visibility = View.VISIBLE
//            }
//            else{
//                orderHistoryAdapter.editBtn.visibility = View.GONE
//
//            }

            orderHistoryAdapter.editBtn.setOnClickListener {
                onTokenPass(task)
            }



            when (task.status) {
                "Pending" -> {
                    orderHistoryAdapter.card.visibility = View.VISIBLE
                    orderHistoryAdapter.pendingStatusText.text = task.status
                    orderHistoryAdapter.status.visibility = View.GONE
                }
                "Completed" -> {
                    orderHistoryAdapter.status.text = task.status
                    orderHistoryAdapter.status.setTextColor(ContextCompat.getColor(context, R.color.greenColor))
                }

                "Cancelled" -> {
                    orderHistoryAdapter.status.text = task.status
                    orderHistoryAdapter.status.setTextColor(ContextCompat.getColor(context, R.color.redColor))
                }

            }

        }

        @SuppressLint("SimpleDateFormat")
        fun getCurrentDate(): String {
            val sdf = SimpleDateFormat("dd MMM yyyy")
            return sdf.format(Date())
        }


    }

}