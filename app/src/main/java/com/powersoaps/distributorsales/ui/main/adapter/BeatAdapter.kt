package com.powersoaps.distributorsales.ui.main.adapter


import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.HomeData
import com.powersoaps.distributorsales.databinding.BeatAdapterBinding
import java.text.DecimalFormat

class BeatAdapter(
    var dataList: ArrayList<HomeData.ShopDetails>,
    var onClicked: (HomeData.ShopDetails) -> Unit,
    var onCountClicked: (String) -> Unit
) :
    RecyclerView.Adapter<BeatAdapter.MyViewHolder>(), Filterable {

    var photosRetailerList: ArrayList<HomeData.ShopDetails> = ArrayList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val beatAdapter =
            BeatAdapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(beatAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position >= 0 && position < dataList.size){
            holder.bindItems(dataList[position])
        }else{
            Log.e("MyAdapter", "Invalid position: $position, size: ${dataList.size}")

        }
    }

    //the class is holding the list view
    inner class MyViewHolder(private val beatAdapter: BeatAdapterBinding) :
        RecyclerView.ViewHolder(beatAdapter.root) {

        @SuppressLint("SetTextI18n")
        fun bindItems(task: HomeData.ShopDetails) {
            val context = beatAdapter.root.context

            if (task.delivery == "Completed") {
                beatAdapter.status.text = "Order Completed"
            }
            else if (task.delivery == "NoOrder") {
                beatAdapter.status.text = "No Order"
                beatAdapter.status.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.skyblue
                    )
                )
            } else {
                beatAdapter.status.text = "Pending"
                beatAdapter.status.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.darkred
                    )
                )
            }
//            in onbinf holder left side is the view whicjh we will implement from adpter item
            beatAdapter.shopname.text = task.shop_name
//            beatAdapter.kms.text = task.shop_distance.toString()+" kms away "
            beatAdapter.kms.text = task.address + "," + task.city + "," + task.pincode
            beatAdapter.category.text = task.category_name
//            beatAdapter.targetAmount.text  = beatAdapter.root.context.getString(R.string.rupee)+" "+task.target_amount
//            beatAdapter.achievedAmount.text  = beatAdapter.root.context.getString(R.string.rupee)+" "+task.achived_amount
            val fmt = DecimalFormat("#,##,###")
            val billamount: Double = task.balance_amount.toDouble() //If your num is in String
            val currency = fmt.format(billamount)
            beatAdapter.balanceAmount.text =
                beatAdapter.root.context.getString(R.string.rupee) + " " + currency
            beatAdapter.itemcount.text = task.items
            beatAdapter.top.setOnClickListener { onClicked(task) }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun appData(list: List<HomeData.ShopDetails>) {
        if (list.isNotEmpty()){
            photosRetailerList = list as ArrayList<HomeData.ShopDetails>
            dataList = photosRetailerList
            notifyDataSetChanged()
            Log.d("TAG", "appData: ${list.size}")
        }else{
            Log.d("TAG", "appData: AppData called with an empty list")

        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(p0: CharSequence?): FilterResults {
                val charString = p0?.toString() ?: ""  // Elvis Operator
                if (charString.isEmpty()) {
                    dataList = photosRetailerList
                } else {
                    if (dataList.isEmpty()) {
                        val filteredList = ArrayList<HomeData.ShopDetails>()
                        photosRetailerList.filter {
                            (it.shop_name.contains(
                                p0!!,
                                ignoreCase = true
                            ))
                        }.forEach { (filteredList.add(it)) }
                        dataList = filteredList
                    } else {
                        val filteredList = ArrayList<HomeData.ShopDetails>()
                        dataList.filter { (it.shop_name.contains(p0!!, ignoreCase = true)) }
                            .forEach { (filteredList.add(it)) }
                        dataList = filteredList
                    }
                }
                return FilterResults().apply { values = dataList }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                dataList = p1?.values as ArrayList<HomeData.ShopDetails>
                onCountClicked(dataList.size.toString())
                notifyDataSetChanged()
            }
        }
    }
}