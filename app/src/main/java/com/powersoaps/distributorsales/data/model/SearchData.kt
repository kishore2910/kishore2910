package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class SearchData(
    @field: SerializedName("status_code")
    val statusCode : Int,

    @field: SerializedName("message")
    val message : String,

    @field: SerializedName("title")
    val title : String,

    @field: SerializedName("data")
    val data : SearchListData
){
    data class SearchListData(
        @field: SerializedName("search_details")
        val searchDetails : ArrayList<SearchDetails>
    )

    data class SearchDetails(
        @field: SerializedName("shopId")
        val shopId : Int = 0,

        @field:SerializedName("shop_id")
        val shop_id: String,

        @field:SerializedName("shop_name")
        val shop_name: String,

        @field: SerializedName("shop_total")
        val shop_total : Int,

        @field: SerializedName("paid_amt")
        val paid_Amount : Int,

        @field:SerializedName("balance_amt")
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

        @field:SerializedName("units_name")
        val units_name: String
    )
}