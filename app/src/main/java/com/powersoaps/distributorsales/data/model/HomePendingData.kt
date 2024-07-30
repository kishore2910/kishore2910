package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class HomePendingData(

    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("data")
    val  data: HomeListPendingData
){
    data class HomeListPendingData(

        @field:SerializedName("shop_details")
        val  shop_Pending_details: ArrayList<ShopPendingDetails>? = null
    )
    data class ShopPendingDetails(
        @field: SerializedName("shopId")
        val shopId : Int = 0,

        @field:SerializedName("shop_id")
        val shop_id: String,

        @field:SerializedName("shop_name")
        val shop_name: String,

        @field:SerializedName("target_amount")
        val target_amount: String,

        @field:SerializedName("achived_amount")
        val achived_amount: Int,

        @field:SerializedName("balance_amount")
        val balance_amount: String,

        @field:SerializedName("category_name")
        val category_name: String,

        @field:SerializedName("delivery")
        val delivery: String,

        @field:SerializedName("items")
        val items: String?=null,

        @field:SerializedName("shop_distance")
        val shop_distance: Int,

        @field:SerializedName("address")
        val address: String,

        @field:SerializedName("pincode")
        val pincode: String,

        @field:SerializedName("city")
        val city: String,

    )
}
