package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class ShopDetailData(

    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("data")
    val data: ShopDetails?= null,
){
    data class ShopDetails(
        @field:SerializedName("shop_name")
        val shop_name: String,

        @field:SerializedName("address")
        val address: String,

        @field:SerializedName("city")
        val city: String,

        @field:SerializedName("shop_type")
        val shop_type: String,

        @field:SerializedName("mobile_number")
        val mobile_number: String,

        @field:SerializedName("bill_amount")
        val bill_amount: String,

        @field:SerializedName("paid_amt")
        val paid_amt: String,

        @field:SerializedName("total_outstanding")
        val total_outstanding: String,

        @field:SerializedName("coordinates")
        val coordinates: String,

        @field:SerializedName("shop_status")
        val shop_status: String
    )
}