package com.powersoaps.distributorsales.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import com.powersoaps.distributorsales.data.AuthRepository
import com.powersoaps.distributorsales.data.model.ShopInsightData
import com.powersoaps.distributorsales.data.model.VersionCheckData

class HomeViewModel : ViewModel() {

    private val authRepository by lazy { AuthRepository() }

    val error by lazy { authRepository.errorString }

    val homeData by lazy { authRepository.HomeList }

    val homePendingData by lazy { authRepository.HomePendingList }

    val daySummaryData by lazy { authRepository.DaySummaryList }

    val skuSummaryData by lazy { authRepository.skuSummaryList }

    val shopDetailData by lazy { authRepository.ShopDetailList }

    val orderPlaceData by lazy { authRepository.placeOrder }

    val orderUpdateData by lazy { authRepository.placeOrderUpdate }

    val previousOrderData by lazy { authRepository.previousData }

    val editOrderData by lazy { authRepository.editOrderData }

    val OrderHistoryData by lazy { authRepository.orderHistoryData }

    val OrderDetailsData by lazy { authRepository.orderDetailsData }

    val ShopTypeData by lazy { authRepository.ShopTypeData }

    val AddShopData by lazy { authRepository.AddShopData }

    val AboutShopData by lazy { authRepository.AboutStoreData }

    val EditShopData by lazy { authRepository.EditShopData }

    val Shopinsightdata by lazy { authRepository.shopInSightData }

    val paymentdata by lazy { authRepository.paymentDetailsData }

    val ordertransdata by lazy { authRepository.ordertrasactiondata }

    val orderdeliverdata by lazy { authRepository.orderdeliverydata }

    val versionCheckResponse: LiveData<VersionCheckData> by lazy { authRepository.versionCheck }

    val bankDetailResponse by lazy { authRepository.bankDetails }

    fun home(employeeId: String, shopId : Int,searchText : String ){
        authRepository.HomeListMethod(employeeId, shopId, searchText)
    }
    fun homePending(employeeId: String, shopId: Int, ){
        authRepository.HomePendingListMethod(employeeId, shopId)
    }

    fun searchApi(employeeId: String, searchText : String){
        authRepository.searchApi(employeeId,searchText)
    }
    fun daySummary(employeeId: String){
        authRepository.DaysummaryListMethod(employeeId)
    }

    fun skuSummary(employeeId: String){
        authRepository.SkusummaryListMethod(employeeId)
    }

    fun shopDetails(employee_id:String,shopId: String){
        authRepository.getShopDetails(employee_id,shopId)
    }

    fun placeOrder(jsonObject: JsonObject){
        authRepository.PlaceOrder(jsonObject)
    }

    fun orderUpdate(jsonObject: JsonObject){
        authRepository.orderUpdate(jsonObject)
    }

    fun previousOrderData(employeeId: String,shop_id: String){
        authRepository.LoadPreviousOrder(employeeId,shop_id)
    }

    fun editOrder(orderToken: String ){
        authRepository.editOrder(orderToken)
    }

    fun OrderHistory(employeeId: String,shopId : String){
        authRepository.OrderHistoryApi(employeeId,shopId)
    }
    fun OrderDetailsApi(employeeId: String,shopId : String,orderId : String){
        authRepository.OrderDetailsApi(employeeId,shopId,orderId)
    }


    fun OrderDeliver(employeeId: String,shopId : String,orderId : String, pending : String){
        authRepository.orderdeliverydata(employeeId,shopId,orderId, pending)
    }

    fun PaymentDetailsApi(employeeId: String,shopId : String,orderId : String){
        authRepository.paymentdetailApi(employeeId,shopId,orderId)
    }
    fun OrderTransactionApi(employeeId: String,shopId : String,orderId : String,amount_paid : String,payment_type : String){
        authRepository.orderTranscationApi(employeeId,shopId,orderId,amount_paid,payment_type)
    }
    fun ShopTypeApi(){
        authRepository.ShopTypeApi()
    }
    fun AddShop(user_token:String,units_id:String,shop_name:String,contact_person:String,contact_number:String,shop_type:String,
                license_number:String,url:String,address:String,city:String,pincode:String,
                shop_lat:String){
        authRepository.AddShopApi(user_token,units_id,shop_name,contact_person,contact_number,shop_type,license_number,url,address,city,pincode,shop_lat)
    }

    fun AboutShop(user_token:String, shop_lat:String){
        authRepository.AboutStore(user_token,shop_lat)
    }

    fun EditShop(user_token:String,shop_id:String,units_id:String,shop_name:String,contact_person:String,contact_number:String,shop_type:String,
                license_number:String,url:String,address:String,city:String,pincode:String,
                shop_lat:String){
        authRepository.EditShopApi(user_token,shop_id,units_id,shop_name,contact_person,contact_number,shop_type,license_number,url,address,city,pincode,shop_lat)
    }
    fun ShopInSight(user_token:String, shop_lat:String){
        authRepository.ShopInsight(user_token,shop_lat)
    }

    fun VersionCheck(version:String)
    {
        authRepository.VersionCheckApi(version)
    }


    fun bankDetails(jsonObject: JsonObject){

        authRepository.bankDetails(jsonObject)
    }





}