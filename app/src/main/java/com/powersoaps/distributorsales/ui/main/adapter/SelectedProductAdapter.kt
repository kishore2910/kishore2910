package com.powersoaps.distributorsales.ui.main.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.ProductData
import com.powersoaps.distributorsales.databinding.SelectedProductBinding
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.OrderSummaryActivity
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.ShopDetailActivity
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.TakeOrderActivity
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.roundToInt

class SelectedProductAdapter  (var loadlist: ArrayList<ProductData.ProductListData>,var onClicked: (ProductData.ProductListData) -> Unit, var totalAmount:()-> Unit) :
    RecyclerView.Adapter<SelectedProductAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val loadingSheetAdapter = SelectedProductBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(loadingSheetAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return loadlist.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(loadlist[position],position)
    }

    //the class is holding the list view
    inner class MyViewHolder(private val selectedProductBinding: SelectedProductBinding) : RecyclerView.ViewHolder(selectedProductBinding.root) {

        @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
        fun bindItems(task: ProductData.ProductListData,pos: Int) {
//            val context = beatAdapter.root.context
            //bind the item here
            var totalPrice:Double=0.0
            var totaldiscount:Double=0.0
            selectedProductBinding.productName.text = task.name
            selectedProductBinding.productId.text = task.added_count+" "+task.added_type
            selectedProductBinding.discounted.text = "("+task.percentage+" "+"Discounted)"
            Log.d("TAG", "taskPercentage: ${task.percentage}")

//            selectedProductBinding.taxAmount.text = task.product_gst+" "+"18% Tax"
            selectedProductBinding.taxAmount.text = "18% Tax"
            selectedProductBinding.nosvalue.text = "| "+task.piece_count+" piece / Box"
            selectedProductBinding.nosvalue.visibility=View.VISIBLE

            if (task.added_type=="Box") {
                if (task.discount_enable==true) {
                    val count:Double= task.added_count!!.toDouble()*task.piece_count!!.toDouble()
                    val countPrice:Double= task.total_cost!!.toDouble()
                     totalPrice += totalPrice+(count*countPrice)
//                    val priceBox = costBox * 0.06
//                    totalPrice = costBox + priceBox
                    totaldiscount=(totalPrice* task.percentage!! /100)
                    totalPrice=totalPrice-totaldiscount
                    Log.d("TAG", "bindItemsIfBox: $totalPrice")

//                    OrderSummaryActivity.finalSummaryCartList[adapterPosition].total_value = totalPrice
                }
                else{
                    val count:Double= task.added_count!!.toDouble()*task.piece_count!!.toDouble()
                    val countPrice:Double= task.total_cost!!.toDouble()
                     totalPrice =totalPrice+(count*countPrice)
//                    val price = cost * 0.06
//                    totalPrice = cost + price
                    Log.d("TAG", "bindItemsELSEBox:$totalPrice ")
//                    OrderSummaryActivity.finalSummaryCartList[adapterPosition].total_value = totalPrice

                }
                val fmttwo = DecimalFormat("#,##,###.##")
                val billamounttwo: Double = totalPrice //If your num is in String
                val currencytwo = fmttwo.format(billamounttwo)

                val decimalFormat = DecimalFormat("#.##")
                decimalFormat.roundingMode = RoundingMode.DOWN // Round down to ensure two decimal places
                val roundedValue = decimalFormat.format(totalPrice).toDouble()
                selectedProductBinding.productPrice.text = selectedProductBinding.root.context.getString(R.string.rupee)+" "+ roundedValue

            }
            else {

                if (task.discount_enable==true) {

                    val count:Double= task.added_count!!.toDouble()
                    val countPrice:Double= task.total_cost!!.toDouble()
                    totalPrice=totalPrice+(count*countPrice)
                    totaldiscount=(totalPrice * task.percentage!!/100)
                    totalPrice=totalPrice-totaldiscount
                    Log.d("TAG", "bindItemsNosElse: $totalPrice")
//                    OrderSummaryActivity.finalSummaryCartList[adapterPosition].total_value = totalPrice
                }
                else {

                    val count:Double= task.added_count!!.toDouble()
                    val countPrice:Double= task.total_cost!!.toDouble()
                    totalPrice += (count * countPrice)
                    Log.d("TAG", "bindItemsNos: $totalPrice")
//                    OrderSummaryActivity.finalSummaryCartList[adapterPosition].total_value = totalPrice
                }
                val fmttwo = DecimalFormat("#,##,###.##")
                val billamounttwo: Double = totalPrice //If your num is in String
                val currencytwo = fmttwo.format(billamounttwo)
//                val value = totalPrice
                val decimalFormat = DecimalFormat("#.##")
                decimalFormat.roundingMode = RoundingMode.DOWN // Round down to ensure two decimal places
                val roundedValue = decimalFormat.format(totalPrice).toDouble()
                selectedProductBinding.productPrice.text = selectedProductBinding.root.context.getString(R.string.rupee)+" "+roundedValue
            }

            if (task.discount_enable==true)
                selectedProductBinding.discounted.visibility=View.VISIBLE
            else selectedProductBinding.discounted.visibility=View.GONE

            if (task.additional_offer!=null && task.tag.equals("Free"))
            {
                selectedProductBinding.productPrice.visibility=View.GONE
                selectedProductBinding.free.visibility=View.VISIBLE
                selectedProductBinding.deleteItem.visibility=View.GONE
                selectedProductBinding.discounted.visibility=View.GONE
            }
            else
            {
                selectedProductBinding.productPrice.visibility=View.VISIBLE
                selectedProductBinding.free.visibility=View.GONE
                selectedProductBinding.deleteItem.visibility=View.VISIBLE
            }

            selectedProductBinding.deleteItem.setOnClickListener{
                loadlist.removeAt(pos)
                for (i in 0 until TakeOrderActivity.adapterArray.size){
                    if (TakeOrderActivity.adapterArray[i].token == task.token){
                        TakeOrderActivity.adapterArray[i].added_count = null
                        TakeOrderActivity.adapterArray[i].added_type = null
                        TakeOrderActivity.adapterArray[i].total_value = 0.0
                        TakeOrderActivity.adapterArray[i].percentage = 0.0
                        TakeOrderActivity.adapterArray[i].discount_enable = false
                        break
                    }
                }
                onClicked(task)
            }
            totalAmount()
        }

    }

}