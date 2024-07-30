package com.powersoaps.distributorsales.ui.main.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.ProductData
import com.powersoaps.distributorsales.databinding.ProductItemBinding
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.TakeOrderActivity
import java.text.DecimalFormat


class ProductItemAdapter(
    var context: Context,
    private var productList: ArrayList<ProductData.ProductListData>,
    private val productItemList: (Int, Double) -> Unit,
    var onProductClicked: (ProductData.ProductListData) -> Unit,
    var onCountClicked: (String) -> Unit,
    var onProductRemoved: (ProductData.ProductListData) -> Unit,
    var onoffer: (ProductData.ProductListData) -> Unit,
    var discount: (Double, Int, ArrayList<ProductData.ProductListData>) -> Unit
) :
    RecyclerView.Adapter<ProductItemAdapter.MyViewHolder>(), Filterable {
    //    val services = arrayListOf("0%","1%", "1.5%", "2%")
    val discountService: ArrayList<String> = ArrayList()
    var productTotal: Double = 0.0
    var currentposition: Int? = null
    var tokenpos: String? = null
    var tokenno: String? = null
    var chooseType: String? = "Nos"
    var currentData: ProductData.ProductListData? = null
    var photosList: ArrayList<ProductData.ProductListData> = ArrayList()
    var facingList: String? = ""
    var values: String? = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val loadingSheetAdapter =
            ProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(loadingSheetAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return productList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(productList[position], position)
        holder.setIsRecyclable(false)
    }


    //the class is holding the list view
    inner class MyViewHolder(private val productItemBinding: ProductItemBinding) :
        RecyclerView.ViewHolder(productItemBinding.root) {

        @SuppressLint("SetTextI18n", "ClickableViewAccessibility", "SuspiciousIndentation")
        fun bindItems(task: ProductData.ProductListData, pos: Int) {
            Log.d("TAG", "bindItems: totalValue ${task.total_value}")

            //bind the item here
            currentData = task
            productItemBinding.productId.text = "HSN : " + task.item_code
            productItemBinding.productName.text = task.name
            val boxVal = task.total_cost!!.toDouble() * 1.toDouble() * task.piece_count!!.toDouble()
            Log.d("TAG", "total_costBind: ${task.total_cost}")
            val decimalFormat = DecimalFormat("#,##,###.##")
            val formattedValue = decimalFormat.format(boxVal)
            Log.d("TAG", "formattedValueBind: $formattedValue")
            productItemBinding.productPrice.text =
                productItemBinding.root.context.getString(R.string.rupee) + " " + formattedValue

            if (task.image?.isEmpty() == true) {
                productItemBinding.productImage.setImageResource(R.drawable.product)

            } else {
                Glide.with(context)
                    .load(Uri.parse(task.image)).apply(RequestOptions.circleCropTransform())
                    .into(productItemBinding.productImage)
            }


            productItemBinding.pcs.text = "| " + task.piece_count + " piece / Box"
            val checkItem = productItemBinding.itemCount.text.toString()

            /* if (checkItem == "" || checkItem == "0"){
                 productItemBinding.discountTextView.visibility = View.INVISIBLE
                 productItemBinding.spinText.visibility = View.INVISIBLE
             }else{
                 productItemBinding.spinText.visibility = View.VISIBLE
                 productItemBinding.discountTextView.visibility = View.VISIBLE
             }*/

            if (task.stock_in_hand!!.toInt() > 0) {
                productItemBinding.outofStock.visibility = View.GONE
            } else {
                productItemBinding.outofStock.visibility = View.GONE

            }

            if (task.IsAdditional_Available == false) {
                productItemBinding.offers.visibility = View.GONE
            } else {
                productItemBinding.offers.visibility = View.VISIBLE
            }


// Set the spinText TextView text
            if (task.percentage == null || task.percentage == 0.0) {
                productItemBinding.spinText.text = "0%"
            } else {
                productItemBinding.spinText.text = "${task.percentage}%"
            }


            productItemBinding.spinText.setOnClickListener {
                productItemBinding.itemCount.requestFocus()
                productItemBinding.itemCount.isCursorVisible = true
                productItemBinding.toggle.performClick()

                val spinnerArrayAdapter: ArrayAdapter<String?> = object : ArrayAdapter<String?>(
                    context,
                    android.R.layout.simple_spinner_dropdown_item,
                    TakeOrderActivity.spinnerData as List<String?>
                ) {
                    override fun isEnabled(position: Int): Boolean {
                        return if (position == 0) {
                            true
                        } else {
                            true
                        }
                    }
                }

                productItemBinding.toggle.adapter = spinnerArrayAdapter

                productItemBinding.toggle.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                        }

                        @SuppressLint("SuspiciousIndentation")
                        override fun onItemSelected(
                            p0: AdapterView<*>?,
                            p1: View?,
                            p2: Int,
                            p3: Long
                        ) {
                            facingList = p0?.getItemAtPosition(p2).toString()
                            Log.d("facingList", "onItemSelected: $facingList")
                            for (i in TakeOrderActivity.spinnerData) {
                                if (i.equals(facingList, true)) {
                                    values = i
                                    print(values)
                                    productItemBinding.spinText.text = values
                                    dropDown(values!!)
                                }
                            }
                        }


                        private fun dropDown(value: String) {
                            if (value != "0%") {

                                val percentageString = value

                                val trimmedString = percentageString.replace("%", "")

                                val percentageDouble = trimmedString.toDoubleOrNull()

                                Log.d("TAG", "dropDown: $percentageDouble")
                                val stringCheck = productItemBinding.itemCount.text.toString()
                                if (stringCheck.isEmpty() || stringCheck == "0") {

                                    productItemBinding.spinText.text = "0%"

                                    Toast.makeText(
                                        productItemBinding.root.context,
                                        "Please Enter Number of Products",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    val total_value: Double

                                    total_value = if (task.added_type == "Nos") {
                                        task.total_cost.toDouble() * stringCheck.toDouble()

                                    } else {
                                        task.total_cost.toDouble() * stringCheck.toDouble() * task.piece_count.toDouble()
                                    }

                                    TakeOrderActivity.adapterArray[adapterPosition].discount_enable =
                                        true
                                    TakeOrderActivity.adapterArray[adapterPosition].percentage =
                                        percentageDouble
                                    TakeOrderActivity.adapterArray[adapterPosition].total_value =
                                        0.0
                                    TakeOrderActivity.adapterArray[adapterPosition].total_value =
                                        total_value
                                }
                            } else if (value == "0%") {
                                TakeOrderActivity.adapterArray[adapterPosition].discount_enable =
                                    false
                                TakeOrderActivity.adapterArray[adapterPosition].percentage = 0.0
//                            productItemBinding.spinText.text = "0%"
                            }
                            val filterArray =
                                TakeOrderActivity.adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }
                            Log.d("TAG", "dropDown: filterArray $filterArray")
                            discount(
                                productTotal, filterArray.size,
                                filterArray as ArrayList<ProductData.ProductListData>
                            )
                        }
                    }
            }


            productItemBinding.offers.setOnClickListener { onoffer(task) }

            if (task.added_count.equals(null)) {
                productItemBinding.itemCount.text!!.clear()
            } else {
                productItemBinding.itemCount.text =
                    Editable.Factory.getInstance().newEditable(task.added_count)
            }

            if (task.added_type != null) {
                try {
                    if (task.added_type == "Nos") {
                        if (task.added_count != null) {
                            val totalPrice =
                                task.total_cost.toDouble() * task.added_count.toString().toDouble()
//                            val totalCost = totalPrice * 0.06
//                            val price = totalCost + totalPrice
                            Log.d("TAG", "bindItems: cal Price $totalPrice")
                            val decimalFormatn = DecimalFormat("#,##,###.##")
                            val formattedValuen = decimalFormatn.format(totalPrice)
                            productItemBinding.productPrice.text =
                                productItemBinding.root.context.getString(R.string.rupee) + " " + formattedValuen
                        } else {
                            productItemBinding.productPrice.text =
                                productItemBinding.root.context.getString(R.string.rupee) + " " + task.total_cost
                        }

                        Log.d("formattedValue", "formattedValue2NOS: ${task.total_cost}")
                        productItemBinding.toggleGroup.check(R.id.nosButton)
                        productItemBinding.nosButton.backgroundTintList =
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.togglebutton
                            )
                        productItemBinding.nosButton.setTextColor(
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.white
                            )
                        )
                        productItemBinding.boxButton.backgroundTintList =
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.white
                            )
                        productItemBinding.boxButton.setTextColor(
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.togglebutton
                            )
                        )
                    } else {
                        if (task.added_count != null) {
                            val priceBox = task.total_cost.toDouble() * (task.added_count.toString()
                                .toDouble() * task.piece_count.toDouble())
//                            val boxPrice = priceBox * 0.06
//                            val price = boxPrice + priceBox
                            Log.d("TAG", "bindItems: cal Price box $priceBox")

                            val decimalFormatx = DecimalFormat("#,##,###.##")
                            val formattedValuex = decimalFormatx.format(priceBox)

                            productItemBinding.productPrice.text =
                                productItemBinding.root.context.getString(R.string.rupee) + " " + formattedValuex
                        } else {

                            var cost = task.total_cost.toDouble()
                            val boxValue = cost * task.piece_count.toDouble()
//                            val totalCost = boxValue * 0.06
//                            cost = totalCost + cost
                            val fmttwo = DecimalFormat("#,##,###.##")
                            val billamounttwo: Double = cost.toDouble() //If your num is in String
                            val currencytwo = fmttwo.format(billamounttwo)
                            Log.d("formattedValue", "formattedValueBOX: $currencytwo ")
                            productItemBinding.productPrice.text =
                                productItemBinding.root.context.getString(R.string.rupee) + " " + currencytwo
                        }

                        productItemBinding.toggleGroup.check(R.id.boxButton)
                        productItemBinding.boxButton.backgroundTintList =
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.togglebutton
                            )
                        productItemBinding.boxButton.setTextColor(
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.white
                            )
                        )
                        productItemBinding.nosButton.backgroundTintList =
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.white
                            )
                        productItemBinding.nosButton.setTextColor(
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.togglebutton
                            )
                        )
                    }
                } catch (addedTypeException: Exception) {
                    Log.d("TAG", "bindItems: $addedTypeException")
                }

            }



            productItemBinding.itemCount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (s.toString().isNotEmpty()) {

                        val buttonId: Int = productItemBinding.toggleGroup.checkedButtonId
                        val button: MaterialButton? = buttonId.let {
                            productItemBinding.toggleGroup.findViewById(it)
                        }
                        var chooseType = ""
                        var total_value = 0.0
                        if (button == productItemBinding.nosButton) {
                            chooseType = "Nos"
//                            total_value=task.total_cost!!.toDouble()*s.toString().toDouble()
                            val total_value = task.total_cost.toDouble() * s.toString().toDouble()
//                            val box = total * 0.06
//                            total_value = box + total
                            val decimalFormatt = DecimalFormat("#.##")
                            val formattedValuet = decimalFormatt.format(total_value)
                            Log.d(
                                "formattedValue",
                                "onTextChangedNosIFonTextChanged: $formattedValuet"
                            )
                            productItemBinding.productPrice.text =
                                productItemBinding.root.context.getString(R.string.rupee) + " " + formattedValuet
                        } else {
                            chooseType = "Box"
//                            total_value=task.total_cost!!.toDouble()*s.toString().toDouble()*task.piece_count!!.toDouble()
                            val total_value = task.total_cost.toDouble() * s.toString()
                                .toDouble() * task.piece_count.toDouble()
//                            val priceBox = total_cost * 0.06
//                             total_value = priceBox + total_cost
                            val decimalFormattx = DecimalFormat("#,##,###.##")
                            val formattedValuetx = decimalFormat.format(total_value)
                            Log.d(
                                "formattedValue",
                                "onTextChangedBoxElseOnTextChanged: $formattedValuetx"
                            )

                            productItemBinding.productPrice.text =
                                productItemBinding.root.context.getString(R.string.rupee) + " " + formattedValuetx

                        }

                        if (s.toString() != "0") {
//                            productItemBinding.discountTextView.visibility = View.VISIBLE
//                            productItemBinding.spinText.visibility = View.VISIBLE
                            TakeOrderActivity.adapterArray[adapterPosition].added_count =
                                s.toString()
                            TakeOrderActivity.adapterArray[adapterPosition].added_type = chooseType
                            TakeOrderActivity.adapterArray[adapterPosition].percentage = 0.0
                            TakeOrderActivity.adapterArray[adapterPosition].discount_enable = false
                            productItemBinding.spinText.text = "0%"

                            if (TakeOrderActivity.adapterArray[adapterPosition].added_type == "Nos") {

                                val total =
                                    +(TakeOrderActivity.adapterArray[adapterPosition].total_cost?.toDouble()
                                        ?.times(TakeOrderActivity.adapterArray[adapterPosition].added_count?.toDouble()!!))!!

                                TakeOrderActivity.adapterArray[adapterPosition].total_value = 0.0
                                TakeOrderActivity.adapterArray[adapterPosition].total_value = total
                            } else {

                                val total =
                                    +(TakeOrderActivity.adapterArray[adapterPosition].total_cost?.toDouble()
                                        ?.times((TakeOrderActivity.adapterArray[adapterPosition].added_count?.toDouble()!! * TakeOrderActivity.adapterArray[adapterPosition].piece_count?.toDouble()!!)))!!


                                TakeOrderActivity.adapterArray[adapterPosition].total_value = 0.0
                                TakeOrderActivity.adapterArray[adapterPosition].total_value = total
                            }

                        }
                    } else if (s.toString() == "") {
//                        productItemBinding.discountTextView.visibility = View.INVISIBLE
//                        productItemBinding.spinText.visibility = View.INVISIBLE
                        TakeOrderActivity.adapterArray[adapterPosition].added_count = ""
                        TakeOrderActivity.adapterArray[adapterPosition].added_type = null
                        TakeOrderActivity.adapterArray[adapterPosition].percentage = 0.0
                        TakeOrderActivity.adapterArray[adapterPosition].total_value = 0.0
                        TakeOrderActivity.adapterArray[adapterPosition].discount_enable = false
                        productItemBinding.spinText.text = "0%"
                        chooseType = "Nos"

                    }

                    val filtered =
                        TakeOrderActivity.adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }

//                    productTotal = -TakeOrderActivity.adapterArray[adapterPosition].total_value

                    Log.d("TAG", "onTextChanged: filtered$filtered")
                    Log.d("TAG", "onTextChanged: AdapterArray ${TakeOrderActivity.adapterArray}")

                    val totalFiltered =
                        TakeOrderActivity.adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }

                    for (i in totalFiltered) {
                        productTotal = +i.total_value
                    }

                    productItemList(filtered.size, productTotal)
                }
            })

            productItemBinding.toggleGroup.addOnButtonCheckedListener(MaterialButtonToggleGroup.OnButtonCheckedListener { group, checkedId, isChecked ->
                val btn: MaterialButton = productItemBinding.toggleGroup.findViewById(checkedId)
                val check: String = productItemBinding.itemCount.text.toString()
                var total_value = 0.0
                if (check.isEmpty() || check == "0") {
                    Toast.makeText(
                        productItemBinding.root.context,
                        "Please Enter Number of Products",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (group.checkedButtonId == R.id.nosButton) {
                        chooseType = "Nos"
//                        total_value=task.total_cost!!.toDouble()*check.toDouble()
                        val total_value = task.total_cost.toDouble() * check.toDouble()
//                        val boxValue = total * 0.06
//                        total_value = total + boxValue
                        val decimalFormattg = DecimalFormat("#.##")
                        val formattedValuetg = decimalFormattg.format(total_value)
                        Log.d("formattedValue", "formattedValueNostoggleGroup: $formattedValuetg ")
                        productItemBinding.productPrice.text =
                            productItemBinding.root.context.getString(R.string.rupee) + " " + formattedValuetg
                        productItemBinding.nosButton.backgroundTintList =
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.togglebutton
                            )
                        productItemBinding.nosButton.setTextColor(
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.white
                            )
                        )
                        productItemBinding.boxButton.backgroundTintList =
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.white
                            )
                        productItemBinding.boxButton.setTextColor(
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.togglebutton
                            )
                        )
                        if (check.isNotEmpty()) {
                            if (check.toInt() != 0) {
                                TakeOrderActivity.adapterArray[adapterPosition].added_type = "Nos"
                                TakeOrderActivity.adapterArray[adapterPosition].percentage = 0.0
                                TakeOrderActivity.adapterArray[adapterPosition].discount_enable =
                                    false
                                productItemBinding.spinText.text = "0%"

                                var total = 0.0

                                if (!TakeOrderActivity.adapterArray[adapterPosition].added_count.isNullOrEmpty()) {
                                    total =
                                        +(TakeOrderActivity.adapterArray[adapterPosition].total_cost?.toDouble()
                                            ?.times(TakeOrderActivity.adapterArray[adapterPosition].added_count?.toDouble()!!))!!
                                }

                                TakeOrderActivity.adapterArray[adapterPosition].total_value = 0.0
                                TakeOrderActivity.adapterArray[adapterPosition].total_value = total
                            }

                        }

                    } else if (group.checkedButtonId == R.id.boxButton) {
                        chooseType = "Box"
                        val total_value =
                            task.total_cost.toDouble() * check.toDouble() * task.piece_count.toDouble()
//                        val boxValue = total * 0.06
//                        total_value = total + boxValue
                        val fmttwo = DecimalFormat("#,##,###.##")
//                        val billamounttwo: Double = total_value //If your num is in String
                        val currencytwo = fmttwo.format(total_value)
                        Log.d("formattedValue", "currencytwoBOXtoggleGroup: $formattedValue ")
                        productItemBinding.productPrice.text =
                            productItemBinding.root.context.getString(R.string.rupee) + " " + currencytwo
                        productItemBinding.boxButton.backgroundTintList =
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.togglebutton
                            )
                        productItemBinding.boxButton.setTextColor(
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.white
                            )
                        )
                        productItemBinding.nosButton.backgroundTintList =
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.white
                            )
                        productItemBinding.nosButton.setTextColor(
                            ContextCompat.getColorStateList(
                                productItemBinding.root.context,
                                R.color.togglebutton
                            )
                        )

                        if (check.isNotEmpty()) {
                            if (check.toInt() != 0) {
                                TakeOrderActivity.adapterArray[adapterPosition].added_type = "Box"
                                TakeOrderActivity.adapterArray[adapterPosition].percentage = 0.0
                                TakeOrderActivity.adapterArray[adapterPosition].discount_enable =
                                    false
                                productItemBinding.spinText.text = "0%"

                                val total =
                                    +(TakeOrderActivity.adapterArray[adapterPosition].total_cost?.toDouble()
                                        ?.times((TakeOrderActivity.adapterArray[adapterPosition].added_count?.toDouble()!! * TakeOrderActivity.adapterArray[adapterPosition].piece_count?.toDouble()!!)))!!

                                Log.d("TAG", "bindItemsTotal: $total ")
                                TakeOrderActivity.adapterArray[adapterPosition].total_value = 0.0
                                TakeOrderActivity.adapterArray[adapterPosition].total_value = total


                            }
                        }

                    }

                }

                val filtered =
                    TakeOrderActivity.adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }
                Log.d("TAG", "bindItems:filtered last $filtered ")

                val totalFiltered =
                    TakeOrderActivity.adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }
                Log.d("Filtered", "totalFiltered: ${totalFiltered.size}")
                Log.d("Filtered", "totalFiltered: $totalFiltered")

                for (i in totalFiltered) {
                    productTotal += i.total_value
                }
                productItemList(filtered.size, productTotal)


            })

        }

    }

    @SuppressLint("NotifyDataSetChanged")
    fun addData(list: List<ProductData.ProductListData>) {
        photosList = list as ArrayList<ProductData.ProductListData>
//        TakeOrderActivity.ListFiltered = photosList
        productList = photosList
        notifyDataSetChanged()
    }

    fun clearData(filterData: ArrayList<ProductData.ProductListData>) {

        productList.clear()
        productList.addAll(filterData)
        notifyDataSetChanged()
    }


    @SuppressLint("NotifyDataSetChanged")
    fun tempData(list: ArrayList<String>) {
        TakeOrderActivity.ListFiltered = photosList
        val filteredList = ArrayList<ProductData.ProductListData>()
        for (temp in list) {
            for (i in 0 until photosList.size) {
                if (photosList[i].category_name?.equals(temp) == true) {
                    filteredList.addAll(listOf(photosList[i]))
                }
            }
        }
        if (filteredList.isEmpty()) {

        } else {
            TakeOrderActivity.ListFiltered = filteredList
        }
        onCountClicked(TakeOrderActivity.ListFiltered.size.toString())
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint?.toString() ?: ""
                if (charString.isEmpty()) {
//                    TakeOrderActivity.ListFiltered = photosList
                    productList = photosList
                } else {
                    if (photosList.isEmpty()) {
                        val filteredList = ArrayList<ProductData.ProductListData>()
                        productList.filter {
                            (it.name!!.contains(constraint!!, ignoreCase = true))
                        }.forEach { filteredList.add(it) }
                        TakeOrderActivity.adapterArray = filteredList
                    } else {
                        val filteredList = ArrayList<ProductData.ProductListData>()
                        productList.filter {
                            (it.name!!.contains(constraint!!, ignoreCase = true))
                        }.forEach { filteredList.add(it) }
                        productList = filteredList
                        TakeOrderActivity.adapterArray = filteredList
                    }

                }
                return FilterResults().apply { values = TakeOrderActivity.adapterArray }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                TakeOrderActivity.adapterArray =
                    results?.values as ArrayList<ProductData.ProductListData>
//                print(TakeOrderActivity.ListFiltered)
                onCountClicked(TakeOrderActivity.adapterArray.size.toString())

                notifyDataSetChanged()
            }
        }
    }
}