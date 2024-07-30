package com.powersoaps.distributorsales.ui.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.HomeData
import com.powersoaps.distributorsales.data.model.HomePendingData
import com.powersoaps.distributorsales.databinding.PendingAdapterBinding
import java.text.DecimalFormat

class PendingAdapter(
    var dataListPending: ArrayList<HomePendingData.ShopPendingDetails>,
    var onClicked: (HomePendingData.ShopPendingDetails) -> Unit,
    var onCountClicked: (String) -> Unit
) : RecyclerView.Adapter<PendingAdapter.MyViewHolder>(), Filterable {

    var pendingArrayList = ArrayList<HomePendingData.ShopPendingDetails>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val pendingAdapter =
            PendingAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(pendingAdapter)
    }

    override fun getItemCount(): Int {
        return dataListPending.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataListPending[position])
        holder.setIsRecyclable(false)
    }

    inner class MyViewHolder(private val pendingAdapterBinding: PendingAdapterBinding) :
        RecyclerView.ViewHolder(pendingAdapterBinding.root) {
        @SuppressLint("SetTextI18n")
        fun bindItems(task: HomePendingData.ShopPendingDetails) {
            val context = pendingAdapterBinding.root.context

//            if (task.delivery == "Completed"){
//                pendingAdapterBinding.completedStatus.visibility = View.VISIBLE
//            }else{
//                pendingAdapterBinding.status.visibility = View.VISIBLE
//
//            }
            pendingAdapterBinding.shopname.text = task.shop_name
//            beatAdapter.kms.text = task.shop_distance.toString()+" kms away "
            pendingAdapterBinding.kms.text =
                task.address.toString() + "," + task.city + "," + task.pincode
            pendingAdapterBinding.category.text = task.category_name
//            beatAdapter.targetAmount.text  = beatAdapter.root.context.getString(R.string.rupee)+" "+task.target_amount
//            beatAdapter.achievedAmount.text  = beatAdapter.root.context.getString(R.string.rupee)+" "+task.achived_amount
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = task.balance_amount.toDouble() //If your num is in String
            val currency = fmt.format(billamount)
            pendingAdapterBinding.balanceAmount.text =
                pendingAdapterBinding.root.context.getString(R.string.rupee) + " " + currency
            pendingAdapterBinding.itemcount.text = task.items
            pendingAdapterBinding.top.setOnClickListener { onClicked(task) }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun appPendingData(list: List<HomePendingData.ShopPendingDetails>) {
        pendingArrayList = list as ArrayList<HomePendingData.ShopPendingDetails>
        dataListPending = pendingArrayList
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0?.toString() ?: ""
                if (charString.isEmpty()) {
                    dataListPending = pendingArrayList
                } else {
                    if (dataListPending.isEmpty()) {
                        val filteredList = ArrayList<HomePendingData.ShopPendingDetails>()
                        pendingArrayList.filter {
                            (it.shop_name.contains(
                                p0!!,
                                ignoreCase = true
                            ))
                        }.forEach { (filteredList.add(it)) }
                        dataListPending = filteredList
                    } else {
                        val filteredList = ArrayList<HomePendingData.ShopPendingDetails>()
                        dataListPending.filter { (it.shop_name.contains(p0!!, ignoreCase = true)) }
                            .forEach { (filteredList.add(it)) }
                        dataListPending = filteredList
                    }
                }
                return FilterResults().apply { values = dataListPending }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                dataListPending = p1?.values as ArrayList<HomePendingData.ShopPendingDetails>
                onCountClicked(dataListPending.size.toString())
                notifyDataSetChanged()
            }
        }
    }
}