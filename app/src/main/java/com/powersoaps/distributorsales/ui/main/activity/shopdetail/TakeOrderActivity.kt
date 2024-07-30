package com.powersoaps.distributorsales.ui.main.activity.shopdetail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.DiscountData
import com.powersoaps.distributorsales.data.model.FilterData
import com.powersoaps.distributorsales.data.model.ProductData
import com.powersoaps.distributorsales.databinding.ActivityTakeOrderBinding
import com.powersoaps.distributorsales.ui.base.BaseActivity
import com.powersoaps.distributorsales.ui.main.activity.internet.NoInternetActivity
import com.powersoaps.distributorsales.ui.main.adapter.FilterAdapter
import com.powersoaps.distributorsales.ui.main.adapter.ProductItemAdapter
import com.powersoaps.distributorsales.ui.utils.Session
import com.powersoaps.distributorsales.ui.utils.Util.Alertdialog
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener
import com.powersoaps.distributorsales.ui.viewmodels.AuthViewModel
import java.text.DecimalFormat
import kotlin.math.roundToInt


class TakeOrderActivity : BaseActivity() {

    private val takeOrderBinding by lazy { ActivityTakeOrderBinding.inflate(layoutInflater) }
    private val productAdapter by lazy { ProductItemAdapter(this, adapterArray, ::totalProduct, ::onProductClicked,::onCountClicked,::onProductRemoved, ::onofferClicked,::onDiscount) }
    private val filterAdapter by lazy { FilterAdapter(categoryList,::onClicked ) }

    private val authViewModel: AuthViewModel by viewModels()
    var type: String = ""
    var orderId : String = ""
    var loadingTakeOrderList  :Boolean = false

    lateinit var tempcategoryList: ArrayList<FilterData>
    companion object
    {
        var is_backPressed : Boolean = false
        lateinit var loadingdata: ArrayList<ProductData.ProductListData>
        lateinit var cartdata: ArrayList<ProductData.ProductListData>
        var ListFiltered: ArrayList<ProductData.ProductListData> = ArrayList()
        lateinit var categoryList: ArrayList<FilterData>
        lateinit var tempcategory: ArrayList<String>
        lateinit var selectedProductToken: ArrayList<String>
        lateinit var selectedCategortFilter: ArrayList<String>
        var selectedScreen:String="TakeOrder"
        var searchText:String=""
        var adapterArray: ArrayList<ProductData.ProductListData> = ArrayList()
        var filterData : ArrayList<ProductData.ProductListData> = ArrayList()
        var searchArray : ArrayList<ProductData.ProductListData> = ArrayList()
        var spinnerData : ArrayList<String> = ArrayList()
    }
    private val filterDialog by lazy { BottomSheetDialog(this, R.style.MyBottomSheetDialogTheme)
        .apply { setContentView(layoutInflater.inflate(R.layout.dialog_filter, null))
        setCancelable(false)} }


    @SuppressLint("SetTextI18n", "SuspiciousIndentation")
    private fun totalProduct(i: Int, j: Double) {
        var total = 0.0
        for (i in adapterArray){
//         total = i.total_value + i.total_value

            Log.d("TAG", "totalProduct: ${  i.total_value}")

        }
        val roundTotal = (total * 100.0).roundToInt() / 100.0
        takeOrderBinding.totalItems.text = "Total Items : $i"
        takeOrderBinding.totalPrice.text = "Total Price : ${takeOrderBinding.root.context.getString(R.string.rupee)} " +roundTotal
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(takeOrderBinding.root)

            val bundle = intent.extras
            if (bundle != null) {
                type = bundle.getString("type").toString()

                orderId = bundle.getString("order_id").toString()

                Log.d("TAG", "TAKEORDER: $type and $orderId")
            }

        if (type == "edit"){
            takeOrderBinding.filterProduct.visibility = View.INVISIBLE
            takeOrderBinding.favourite.visibility = View.INVISIBLE
        } else{
            takeOrderBinding.filterProduct.visibility = View.VISIBLE
            takeOrderBinding.favourite.visibility = View.VISIBLE
        }

            authViewModel.productData.observe(this@TakeOrderActivity, productResponse)
            authViewModel.discountData.observe(this@TakeOrderActivity,discountResponse)
            getApiCallSheet()
            loadingsheetdata()



//        }

        takeOrderBinding.back.setOnClickListener {
            onBackPressed()
        }



        //cartList
        takeOrderBinding.addToCartButton.setOnDebounceListener {

            takeOrderBinding.searchView.setQuery("",true)
            adapterArray.distinctBy { it.token }

            val cartItems = adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }

            Log.d("TAG", "adapterArray: $adapterArray ")

            if (cartItems.isEmpty()){
                Alertdialog("Please add a products to cart" ,"Alert")
                takeOrderBinding.cartCount.text = "0"
            }else{
                takeOrderBinding.cartCount.text = cartItems.size.toString()
                takeOrderBinding.cartCount.visibility = View.VISIBLE
                Toast.makeText(this, "Product successfully added to cart", Toast.LENGTH_LONG).show()
            }
        }

        takeOrderBinding.cart.setOnDebounceListener {

            val filtered = adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }
            Log.d("TAG", "filteredArray: $filtered ")
            if (filtered.isNotEmpty()){
                if (type == "edit"){
//                    type = ""
                    startActivity(Intent(this@TakeOrderActivity, OrderSummaryActivity::class.java)
                        .putExtra("type","edit")
                        .putExtra("order_id",orderId))
                } else{
                    startActivity(Intent(this@TakeOrderActivity, OrderSummaryActivity::class.java))
                }
            }
            else
                Toast.makeText(this, "Please add a products to cart", Toast.LENGTH_SHORT).show()
        }

        takeOrderBinding.filterProduct.setOnClickListener {
            bottomSheet()
        }

        takeOrderBinding.favourite.setOnClickListener {
            val cartItems = adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }
            if (cartItems.isEmpty()){
                if(ShopDetailActivity.loadPreviousData.size == 0){
                    Alertdialog("You don't have any favourites","")
                }else{

                    if (!loadingTakeOrderList){
                        type = "previous"
                        Toast.makeText(this, "Your Previous Order Fetched", Toast.LENGTH_SHORT).show()
                        takeOrderBinding.favourite.setImageResource(R.drawable.favourite_select)
                        loadingTakeOrderList = true
                        takeOrderBinding.filterProduct.visibility = View.INVISIBLE
                        getApiCallSheet()
                    }else{
                        type = "fresh"
                        loadingTakeOrderList = false
                        takeOrderBinding.favourite.setImageResource(R.drawable.favourite)
                        takeOrderBinding.filterProduct.visibility = View.VISIBLE
                        getApiCallSheet()
                    }
                }
            }else{
                if (type == "previous"){
                    type = "fresh"
                    loadingTakeOrderList = false
                    takeOrderBinding.favourite.setImageResource(R.drawable.favourite)
                    takeOrderBinding.filterProduct.visibility = View.VISIBLE
                    takeOrderBinding.cartCount.visibility =View.GONE
                    getApiCallSheet()
                }else{
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setMessage("If you select favourites previous order will be updated")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                            dialog.dismiss()
                        })
                        .setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, which ->
                            if(ShopDetailActivity.loadPreviousData.size == 0){
                                Alertdialog("You don't have any favourites","")
                            }else {
                                type = "previous"
                                Toast.makeText(this, "Your Previous Order Fetched", Toast.LENGTH_SHORT).show()
                                takeOrderBinding.favourite.setImageResource(R.drawable.favourite_select)
                                loadingTakeOrderList = true
                                getApiCallSheet()
                                dialog.dismiss()
                            }
                        })
                        .show()
                }
            }
        }

        takeOrderBinding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            @SuppressLint("SetTextI18n")
            override fun onQueryTextSubmit(search: String): Boolean {
                if (search.isNotEmpty()) {
                    if (search.length >= 3) {
                        searchText="Searching"
                        productAdapter.filter.filter(search)
                    }
                }
                else {
                    searchText=""
                    if (selectedCategortFilter.isEmpty()){
                        productAdapter.addData(loadingdata)
                        adapterArray.clear()
                        filterData.clear()
                        adapterArray.addAll(loadingdata)
                        takeOrderBinding.productItem.adapter = productAdapter
                        takeOrderBinding.totalProduct.text = "Total Products : " + loadingdata.size.toString()

                    }else{
                        productAdapter.tempData(selectedCategortFilter)
                        if (filter(selectedCategortFilter).isNotEmpty()) {
                            adapterArray.clear()
                            adapterArray.addAll(filterData)
                            takeOrderBinding.productItem.adapter = productAdapter

                        }
                    }
                }
                return false
            }
            @SuppressLint("SetTextI18n")
            override fun onQueryTextChange(search: String): Boolean {
                if (search.length != 0) {
                    if (search.length >= 3) {
                        productAdapter.filter.filter(search)
                    }
                }
                else {
                    if (selectedCategortFilter.isEmpty()){
                        adapterArray.clear()
                        adapterArray.addAll(loadingdata)
                        productAdapter.addData(loadingdata)
                        takeOrderBinding.totalProduct.text = "Total Products : " + loadingdata.size.toString()
                        takeOrderBinding.productItem.adapter = productAdapter
                    }else{
                        productAdapter.tempData(selectedCategortFilter)
                    }
                }
                return false
            }
        })

        takeOrderBinding.clearFilter.setOnClickListener {
            selectedCategortFilter.clear()
            for (e in 0 until categoryList.size)
            {
                categoryList[e] = FilterData(categoryList[e].filterName,false)
            }
            takeOrderBinding.filterCount.visibility=View.GONE
            takeOrderBinding.clearFilter.visibility=View.GONE
            filterData.clear()
            adapterArray.clear()
            adapterArray.addAll(loadingdata)
            takeOrderBinding.totalProduct.text="Total Products : "+ adapterArray.size.toString()
            takeOrderBinding.productItem.adapter = productAdapter
        }
    }

    private fun getApiCallSheet() {
        if (isNetworkConnected(this)) {
            loadingDialog(true)
            authViewModel.productList(Session.userToken)
            authViewModel.discountPercentage(Session.userToken)
        } else startActivity(Intent(this, NoInternetActivity::class.java))
    }

    private fun loadingsheetdata() {
        loadingdata = ArrayList()
        cartdata = ArrayList()
        ListFiltered = ArrayList()
        categoryList = ArrayList()
        tempcategoryList = ArrayList()
        tempcategory = ArrayList()
        selectedProductToken = ArrayList()
        selectedCategortFilter= ArrayList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }


    @SuppressLint("CutPasteId", "SetTextI18n")
    private fun bottomSheet() {
        filterDialog.show()
        val filterButton=filterDialog.findViewById<MaterialButton>(R.id.filterButton)
        val closeButton=filterDialog.findViewById<ImageButton>(R.id.closeRequestSuccess)
        closeButton?.setOnClickListener {
            selectedCategortFilter.clear()
            for (e in 0 until categoryList.size) {
                categoryList.set(e, FilterData(categoryList[e].filterName, false))
            }
            takeOrderBinding.filterCount.visibility = View.GONE
            takeOrderBinding.clearFilter.visibility = View.GONE
            productAdapter.addData(loadingdata)
            takeOrderBinding.totalProduct.text = "Total Products : " + loadingdata.size.toString()
            takeOrderBinding.productItem.adapter = productAdapter
            filterDialog.dismiss()
        }

        filterButton?.setOnDebounceListener {
            if (selectedCategortFilter.size!=0) {
                if (filter(selectedCategortFilter).isNotEmpty()){
                    adapterArray.clear()
                    adapterArray.addAll(filterData)
                    takeOrderBinding.productItem.adapter = productAdapter
                }
                filterDialog.dismiss()
            }
            else
            {
                Toast.makeText(this, "Please Select Filter", Toast.LENGTH_SHORT).show()
            }
            if (selectedCategortFilter.size!=0)
            {
                takeOrderBinding.filterCount.visibility= View.VISIBLE
                takeOrderBinding.clearFilter.visibility= View.VISIBLE

                takeOrderBinding.filterCount.text=selectedCategortFilter.size.toString()
                takeOrderBinding.totalProduct.text = "Total Products : ${filterData.size}"
            }
            else {
                takeOrderBinding.filterCount.visibility=View.GONE
                takeOrderBinding.clearFilter.visibility=View.GONE
            }

        }

        val filterlistview=filterDialog.findViewById<RecyclerView>(R.id.filterItem)
        val layoutManager = FlexboxLayoutManager(this)
        layoutManager.setFlexDirection(FlexDirection.ROW)
        layoutManager.setAlignItems(AlignItems.FLEX_START)
        filterlistview!!.layoutManager = layoutManager
        filterlistview.adapter=filterAdapter
        if (selectedCategortFilter.size!=0) {
            takeOrderBinding.filterCount.visibility=View.VISIBLE
            takeOrderBinding.clearFilter.visibility=View.VISIBLE
            takeOrderBinding.filterCount.text=selectedCategortFilter.size.toString()
        }
        else {
            takeOrderBinding.filterCount.visibility=View.GONE
            takeOrderBinding.clearFilter.visibility=View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onClicked(data: FilterData,selected:Boolean,pos:Int){
        if (selectedCategortFilter.size == 0) {
            selectedCategortFilter.add(data.filterName)
        } else {
            for (i in 0 until selectedCategortFilter.size)
                if (selectedCategortFilter[i] == data.filterName) {
                    selectedCategortFilter.removeAt(i)
                    break
                } else if (!selectedCategortFilter.contains(data.filterName)){
                    selectedCategortFilter.add(data.filterName)
                    break
                }
        }
        filterAdapter.notifyDataSetChanged()
    }

    private  fun onofferClicked(data: ProductData.ProductListData){
        Alertdialog(data.name!!,data.additional_offer!!)
    }

    @SuppressLint("SetTextI18n")
    private fun onDiscount(i:Double , filter:Int, filterArray:ArrayList<ProductData.ProductListData>){
    var totalAmt = 0.0
        for (j in filterArray){
            for (k in adapterArray){
                if (j.token == k.token){
                    try {
                        totalAmt += k.total_value - (k.total_value * (k.percentage!!.toDouble() / 100))

                    }catch(e :Exception){
                        Log.d("TAG", "onDiscount:$e ")
                    }
                }
            }

        }
        val roundTotal = (totalAmt * 100.0).roundToInt() / 100.0
        takeOrderBinding.totalItems.text = "Total Items : $filter"
        takeOrderBinding.totalPrice.text = "Total Price : ${takeOrderBinding.root.context.getString(R.string.rupee)}   " +roundTotal

    }


    @SuppressLint("SetTextI18n")
    private fun onProductClicked(data: ProductData.ProductListData){


        if (cartdata.size!=0)
        {
            for (i in 0 until cartdata.size)
            {
                if (selectedProductToken.contains(data.token))
                {
                    if (cartdata[i].token.equals(data.token))
                    {
                        cartdata.set(i,data)
                    }
                }
                else
                {
                    cartdata.add(data)
                    selectedProductToken.add(data.token.toString())
                }
            }
        }
        else
        {
            cartdata.add(data)
            selectedProductToken.add(data.token.toString())
        }
        System.out.println(cartdata)

        var totalPrice:Double=0.0
        for (j in 0 until cartdata.size)
        {
            if (cartdata[j].added_type==null || cartdata[j].added_type=="Nos" )
            {
                val count:Double=cartdata[j].added_count!!.toDouble()
                val countPrice:Double=cartdata[j].total_cost!!.toDouble()
                totalPrice += (count * countPrice)
                if (cartdata[j].discount_enable)
                {
                    val dis=((count*countPrice)* cartdata[j].percentage!!/100)
                    totalPrice -= dis
                }
            }
            else
            {
                val count:Double=cartdata[j].added_count!!.toDouble()*cartdata[j].piece_count!!.toDouble()
                val countPrice:Double=cartdata[j].total_cost!!.toDouble()
                totalPrice=totalPrice+(count*countPrice)
                if (cartdata[j].discount_enable) {
                    val dis=((count*countPrice)* cartdata[j].percentage!!/100)
                    totalPrice -= dis
                }
            }
        }
        val fmttwo = DecimalFormat("#,##,###")
        val billamounttwo: Double = totalPrice.toDouble() //If your num is in String
        val currencytwo = fmttwo.format(billamounttwo)
        ShopDetailActivity.loadTotalAmount=currencytwo
//        takeorderBinding.totalPrice.text=applicationContext.getString(R.string.rupee)+" "+ShopDetailActivity.loadTotalAmount
//        takeorderBinding.totalItems.text= "Total Items : "+cartdata.size.toString()

    }

    @SuppressLint("SetTextI18n")
    private fun onProductRemoved(data: ProductData.ProductListData){
        try {
            if (cartdata.size!=0) {
                for (i in 0 until cartdata.size) {
                    if (selectedProductToken.contains(data.token)) {
                        if (cartdata[i].token.equals(data.token)) {
                            cartdata.removeAt(i)
                            break
                        }
                    }
                }
            }

            for (j in 0 until selectedProductToken.size)
            {
                if (selectedProductToken[j].equals(data.token))
                {
                    selectedProductToken.removeAt(j)
                    break
                }
            }
            System.out.println(cartdata)

            var totalPrice:Double=0.0
            for (j in 0 until cartdata.size)
            {
                if (cartdata[j].added_type.equals("Nos"))
                {
                    val count:Double=cartdata[j].added_count!!.toDouble()
                    val countPrice:Double=cartdata[j].total_cost!!.toDouble()
                    totalPrice += (count * countPrice)
                    if (cartdata[j].discount_enable)
                    {
                        val dis=((count*countPrice) * cartdata[j].percentage!!/100)
                        totalPrice -= dis
                        Log.d("TAG", "onProductRemoved: $totalPrice")
                    }
                }
                else
                {
                    val count:Double=cartdata[j].added_count!!.toDouble()*cartdata[j].piece_count!!.toDouble()
                    val countPrice:Double=cartdata[j].total_cost!!.toDouble()
                    totalPrice=totalPrice+(count*countPrice)
                    if (TakeOrderActivity.cartdata[j].discount_enable)
                    {
                        val dis=((count*countPrice) * cartdata[j].percentage!!/100)
                        totalPrice -= dis
                    }
                }
            }

            val fmttwo = DecimalFormat("#,##,###")
            val billamounttwo: Double = totalPrice.toDouble() //If your num is in String
            val currencytwo = fmttwo.format(billamounttwo)
                ShopDetailActivity.loadTotalAmount=currencytwo
            takeOrderBinding.totalPrice.text=applicationContext.getString(R.string.rupee)+" "+ ShopDetailActivity.loadTotalAmount
            takeOrderBinding.totalItems.text= "Total Items : "+cartdata.size.toString()
        }catch (e:Exception) {
            print(e.toString())
        }

    }

    @SuppressLint("SetTextI18n")
    private fun onCountClicked(size: String){
        if (size == "0") Toast.makeText(this, "Product Not found", Toast.LENGTH_SHORT).show()
//        takeorderBinding.totalProduct.text="Total Products : "+size
        takeOrderBinding.totalPrice.text=applicationContext.getString(R.string.rupee)+" "+ ShopDetailActivity.loadTotalAmount
        takeOrderBinding.totalItems.text= "Total Items : "+cartdata.size.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun onTotalItemsCount(size: String){
        takeOrderBinding.totalItems.text="Total Products : "+ cartdata.size.toString()
    }

    @SuppressLint("SetTextI18n")
    private val productResponse = Observer<ProductData> {
        if (it.status_code == 200) {
            loadingdata.clear()
            for(list in 0 until it.data!!.size)
            {

                loadingdata.add(it.data[list])

                if (tempcategory.size==0)
                {
                    tempcategory.add(it.data[list].category_name.toString())
//                    categoryList.add(FilterData(it.data[list].category_name.toString(),false))
                }
                else
                {
                    if (!tempcategory.contains(it.data[list].category_name.toString()))
                    {
                        tempcategory.add(it.data[list].category_name.toString())
//                        categoryList.add(FilterData(it.data[list].category_name.toString(),false))
                    }
                }
                categoryList.add(FilterData(it.data[list].category_name.toString(),false))
            }
            Log.d("TAG", "loadingdata: $loadingdata")

            val distinct = categoryList.distinct()
            categoryList.clear()
            categoryList.addAll(distinct)

            System.out.println(loadingdata+tempcategory)

            if (type=="previous") {

                for (f in 0 until loadingdata.size)
                {
                    for (e in 0 until ShopDetailActivity.loadPreviousData.size)
                    {

                        if (it.data[f].token == ShopDetailActivity.loadPreviousData[e].product_token) {

                            var totalPrice:Double=0.0
                            var totaldiscount:Double=0.0
                            var currencytwo = 0.0
                            if (ShopDetailActivity.loadPreviousData[e]!!.units=="Box") {
                                if (it.data[f].discount_enable==true) {

                                    val count:Double= ShopDetailActivity.loadPreviousData[e]!!.quantity!!.toDouble()*it.data[f].piece_count!!.toDouble()
                                    val countPrice:Double= it.data[f].total_cost!!.toDouble()
                                    totalPrice=totalPrice+(count*countPrice)
                                    totaldiscount=(totalPrice*it.data[f].percentage!!/100)
                                    totalPrice=totalPrice-totaldiscount

                                }
                                else{
                                    val count:Double= ShopDetailActivity.loadPreviousData[e]!!.quantity!!.toDouble()*it.data[f].piece_count!!.toDouble()
                                    val countPrice:Double= it.data[f].total_cost!!.toDouble()
                                    totalPrice=totalPrice+(count*countPrice)

                                }
                                currencytwo  = totalPrice //If your num is in String

                            }
                            else
                            {
                                if (it.data[f].discount_enable==true) {

                                    val count:Double= ShopDetailActivity.loadPreviousData[e]!!.quantity!!.toDouble()
                                    val countPrice:Double= it.data[f].total_cost!!.toDouble()
                                    totalPrice=totalPrice+(count*countPrice)
                                    totaldiscount=(totalPrice * it.data[f].percentage!!/100)
                                    totalPrice=totalPrice-totaldiscount

                                }
                                else {
                                    val count:Double= ShopDetailActivity.loadPreviousData[e]!!.quantity!!.toDouble()
                                    val countPrice:Double= it.data[f].total_cost!!.toDouble()
                                    totalPrice=totalPrice+(count*countPrice)
                                }
                                currencytwo = totalPrice //If your num is in String
                            }

                                loadingdata.set(f,ProductData.ProductListData(
                                    it.data[f].category_token,  it.data[f].category_name,  it.data[f].token,
                                    it.data[f].name,  it.data[f].item_code,it.data[f].total_cost,  it.data[f].product_gst,
                                    it.data[f].net_weight,ShopDetailActivity.loadPreviousData[e]!!.quantity,
                                    ShopDetailActivity.loadPreviousData[e]!!.units, it.data[f].piece_count,
                                    it.data[f].stock_in_hand,it.data[f].additional_offer,it.data[f].discount_enable,
                                    0.0,
                                    currencytwo, it.data[f].IsAdditional_Available,
                                    it.data[f].BoxMaxCountOffer,it.data[f].BoxMinCountOffer,it.data[f].tag,it.data[f].is_free,it.data[f].image
                                ))
                            Log.d("TAG", "loadingdataPrevious: $loadingdata")
                                cartdata.add(ProductData.ProductListData(
                                    it.data[f].category_token,  it.data[f].category_name,  it.data[f].token,
                                    it.data[f].name,  it.data[f].item_code,it.data[f].total_cost,  it.data[f].product_gst,
                                    it.data[f].net_weight,ShopDetailActivity.loadPreviousData[e].quantity,
                                    ShopDetailActivity.loadPreviousData[e].units, it.data[f].piece_count,
                                    it.data[f].stock_in_hand, it.data[f].additional_offer, it.data[f].discount_enable,
                                    it.data[f].percentage,
                                    currencytwo, it.data[f].IsAdditional_Available,
                                    it.data[f].BoxMaxCountOffer,it.data[f].BoxMinCountOffer,it.data[f].tag,it.data[f].is_free,it.data[f].image
                                ))
                            Log.d("TAG", "cartdataPrevious: $cartdata")
                                selectedProductToken.add(it.data[f].token!!)
                                break
//                            }
                        }else {
                            Log.d("TAG", "DataNotFound:No DAta ")
                        }
                    }
                }
            }


            if (type == "edit"){
                for (f in 0 until loadingdata.size)
                {
                    for (e in 0 until OrderHistoryActivity.editPreviousData.size)
                    {

                        if (it.data[f].token == OrderHistoryActivity.editPreviousData[e].product_token) {

                            var totalPrice:Double=0.0
                            var totaldiscount:Double
                            var currencytwo: Double
                            if (OrderHistoryActivity.editPreviousData[e].units=="Box") {
                                if (OrderHistoryActivity.editPreviousData[e].discountEnable == true) {

                                    val count:Double= OrderHistoryActivity.editPreviousData[e]!!.quantity!!.toDouble()*it.data[f].piece_count!!.toDouble()
                                    val countPrice:Double= it.data[f].total_cost!!.toDouble()
                                    totalPrice=totalPrice+(count*countPrice)
                                    totaldiscount=(totalPrice * OrderHistoryActivity.editPreviousData[e].discount!!.toDouble() /100)
                                    totalPrice=totalPrice-totaldiscount

                                    Log.d("TAG", "totalPriceIF: $totalPrice ")

                                    Log.d("TAG", "discount_enable: count Box $count")
                                    Log.d("TAG", "discount_enable: total $totalPrice")

                                }
                                else{
                                    val count:Double= OrderHistoryActivity.editPreviousData[e]!!.quantity!!.toDouble()*it.data[f].piece_count!!.toDouble()
                                    val countPrice:Double= it.data[f].total_cost!!.toDouble()
                                    totalPrice=totalPrice+(count*countPrice)
                                    Log.d("TAG", "totalPriceELSE: $totalPrice ")
                                }
                                currencytwo  = totalPrice //If your num is in String

                                Log.d("TAG", "currencytwoElse: $currencytwo")
                            }
                            else {
                                if (OrderHistoryActivity.editPreviousData[e].discountEnable == true) {

                                    val count:Double= OrderHistoryActivity.editPreviousData[e]!!.quantity!!.toDouble()
                                    val countPrice:Double= it.data[f].total_cost!!.toDouble()
                                    totalPrice=totalPrice+(count*countPrice)
                                    totaldiscount=(totalPrice * OrderHistoryActivity.editPreviousData[e].discount!!.toDouble()/100)
                                    totalPrice=totalPrice-totaldiscount

                                    Log.d("TAG", "discount_enablecount : $count")
                                    Log.d("TAG", "discount_enabletotal : $totalPrice")


                                }
                                else {
                                    val count:Double= OrderHistoryActivity.editPreviousData[e]!!.quantity!!.toDouble()
                                    val countPrice:Double= it.data[f].total_cost!!.toDouble()
                                    totalPrice=totalPrice+(count*countPrice)

                                }
                                currencytwo = totalPrice //If your num is in String
                            }

                            loadingdata.set(f,ProductData.ProductListData(
                                it.data[f].category_token, it.data[f].category_name, it.data[f].token,
                                it.data[f].name, it.data[f].item_code, it.data[f].total_cost,
                                it.data[f].product_gst, it.data[f].net_weight,
                                OrderHistoryActivity.editPreviousData[e].quantity,
//                                it.data[f].added_type,
//                                OrderHistoryActivity.editPreviousData[e]!!.totalValue,
                                OrderHistoryActivity.editPreviousData[e].units, it.data[f].piece_count,
                                it.data[f].stock_in_hand, it.data[f].additional_offer,
                                OrderHistoryActivity.editPreviousData[e].discountEnable,
                                OrderHistoryActivity.editPreviousData[e].discount!!.toDouble(),
                                totalPrice, it.data[f].IsAdditional_Available, it.data[f].BoxMaxCountOffer,
                                it.data[f].BoxMinCountOffer, it.data[f].tag, it.data[f].is_free, it.data[f].image
                            ))
                            Log.d("TAG", "loadingdata: $loadingdata")

                            cartdata.add(ProductData.ProductListData(
                                it.data[f].category_token,  it.data[f].category_name,  it.data[f].token,
                                it.data[f].name,  it.data[f].item_code,it.data[f].total_cost,  it.data[f].product_gst,
                                it.data[f].net_weight,OrderHistoryActivity.editPreviousData[e].quantity,
                                OrderHistoryActivity.editPreviousData[e].units, it.data[f].piece_count,
                                it.data[f].additional_offer,
//                                it.data[f].added_type,
                                it.data[f].stock_in_hand,OrderHistoryActivity.editPreviousData[e].discountEnable,
                                OrderHistoryActivity.editPreviousData[e]!!.discount!!.toDouble(),
                                totalPrice,
                                it.data[f].IsAdditional_Available,
                                it.data[f].BoxMaxCountOffer,it.data[f].BoxMinCountOffer,it.data[f].tag,it.data[f].is_free,it.data[f].image
                            ))

                            Log.d("TAG", "cartdata: $cartdata")

                            selectedProductToken.add(it.data[f].token!!)
                            break
//                            }
                        }else {
                            Log.d("TAG", "DataNotFound:No DAta ")
                        }
                    }
                }
            }


            adapterArray.clear()
            adapterArray.addAll(loadingdata)
            productAdapter.addData(adapterArray)
            Log.d("TAG", "loadingdata: adapterArray $adapterArray")
//            adapterArray.addAll(loadingdata)
            takeOrderBinding.productItem.adapter = productAdapter
            if (type=="previous")
            {
                takeOrderBinding.totalProduct.text="Total Products : "+loadingdata.size.toString()
                takeOrderBinding.totalItems.text= "Total Items : "+ShopDetailActivity.loadPreviousData.size.toString()
                takeOrderBinding.totalPrice.text=applicationContext.getString(R.string.rupee)+" "+ShopDetailActivity.loadTotalAmount
            }
            else
            {
                takeOrderBinding.totalProduct.text="Total Products : "+loadingdata.size.toString()
                ShopDetailActivity.loadTotalAmount="0"
                takeOrderBinding.totalPrice.text=applicationContext.getString(R.string.rupee)+" "+ShopDetailActivity.loadTotalAmount
            }
            loadingDialog(false)
        } else {
            loadingDialog(false)
            Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
        }
    }


    val discountResponse = Observer<DiscountData> {
        spinnerData.clear()
        if (it.status_code == 200){
            Log.d("TAG", "discountResponse: ${it.data}")
            spinnerData.addAll(it.data)
            Log.d("TAG", "spinnerData: $spinnerData")

        }
    }

    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (selectedScreen!="TakeOrder")
        {
            takeOrderBinding.totalItems.text= "Total Items : "+cartdata.size.toString()
            takeOrderBinding.totalPrice.text=applicationContext.getString(R.string.rupee)+" "+ShopDetailActivity.loadTotalAmount
            productAdapter.addData(adapterArray)
            selectedScreen="TakeOrder"

            val cartItems = adapterArray.filter { !it.added_count.isNullOrEmpty() && it.added_count != "0" }
            if (cartItems.isEmpty()){
//                Alertdialog("Please add a products to place order")
                takeOrderBinding.cartCount.visibility =View.GONE
            }else{
                takeOrderBinding.cartCount.text = cartItems.size.toString()
            }

        }
        takeOrderBinding.productItem.adapter = productAdapter

//        takeorderBinding.totalItems.text= "Total Items : "+cartdata.size.toString()
//        takeorderBinding.totalPrice.text=applicationContext.getString(R.string.rupee)+" "+ShopDetailActivity.loadTotalAmount
//        productAdapter.addData(adapterArray)
    }




    private fun filter(filter: ArrayList<String>) : ArrayList<ProductData.ProductListData>{

        if (filterData.isNotEmpty()){
            for (i in categoryList){
                if (!i.selected){
                    for (j in adapterArray){
                        if (i.filterName == j.category_name){
                            j.added_count = ""
                            j.added_type = "Box"
                            j.percentage = 0.0
                        }
                    }
                }
            }
            productAdapter.clearData(adapterArray)
            filterData.clear()
            filterData.addAll(adapterArray)
        }else {
            for (j in adapterArray){
                j.added_count = ""
                j.added_type = "Box"
                j.percentage = 0.0
            }
            productAdapter.clearData(adapterArray)
            filterData.clear()
        }
        for (temp in filter){
            for (i in loadingdata){
                if (temp ==  i.category_name){
                    Log.d("filterData", "filter: $i")
                    filterData.add(i)
                }
            }
        }

        return filterData
    }


}