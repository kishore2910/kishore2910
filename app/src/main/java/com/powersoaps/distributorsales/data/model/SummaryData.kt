package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class SummaryData (

    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("data")
    val  data: ArrayList<DaySummmaryList>? = null
    ){
    data class DaySummmaryList(
        @field:SerializedName("shop_name")
        val shop_name: String,

        @field:SerializedName("quantity")
        val quantity: String,

        @field:SerializedName("sale_amount")
        val sale_amount: String,

        @field:SerializedName("shop_type")
        val shop_type: String?=null,
    )

}