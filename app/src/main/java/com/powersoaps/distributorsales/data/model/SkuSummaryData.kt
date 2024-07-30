package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class SkuSummaryData(

    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("data")
    val  data: dataobject ? = null
){
    data class dataobject(
        @field:SerializedName("product_count")
        val productdetails: product_details ? = null ,

        @field:SerializedName("product_details")
        val  skulist: ArrayList<SkuSummaryList>? = null
    )
    {
        data class product_details(
            @field:SerializedName("product_count")
            val product_count: String?= null
        )
        data class SkuSummaryList(

            @field:SerializedName("product_name")
            val product_name: String,

            @field:SerializedName("sales_amt")
            val sales_amt: String,

            @field:SerializedName("box_value")
            val box_value: Int,

            @field:SerializedName("quantity_value")
            val quantity_value: String,

            @field:SerializedName("quantity_units")
            val quantity_units: String
        )
    }
}