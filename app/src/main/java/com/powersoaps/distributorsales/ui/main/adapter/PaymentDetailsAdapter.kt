package com.powersoaps.distributorsales.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.powersoaps.distributorsales.data.model.PaymentDetailsData
import com.powersoaps.distributorsales.databinding.PaymentdetailsAdapterBinding
import java.text.DecimalFormat

class PaymentDetailsAdapter (var paymentdetailslist: ArrayList<PaymentDetailsData.dataobject.Overall_payment_details>) :
    RecyclerView.Adapter<PaymentDetailsAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val paymentDetailsAdapter = PaymentdetailsAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(paymentDetailsAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return paymentdetailslist.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(paymentdetailslist[position])
    }

    //the class is holding the list view
    inner class MyViewHolder(private val paymentDetailsAdapter: PaymentdetailsAdapterBinding) : RecyclerView.ViewHolder(paymentDetailsAdapter.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(task: PaymentDetailsData.dataobject.Overall_payment_details) {
//            val context = beatAdapter.root.context
            //bind the item here
            paymentDetailsAdapter.payment.text = task.payment
            paymentDetailsAdapter.paymentcurrentdate.text = ":"+" "+task.date_time
            paymentDetailsAdapter.paymentmodetype.text  = ":"+" "+task.payment_mode
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = task.amount.toDouble() //If your num is in String
            val currency = fmt.format(billamount)
            paymentDetailsAdapter.paymentmodeamount.text  = ":"+" "+"₹"+" "+currency
        }

    }

}