package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class ShopInsightData(
    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("data")
    val data: ShopInSight?= null,
){
    data class ShopInSight(
        @field:SerializedName("monthly_items")
        val monthly_items: String,

        @field:SerializedName("full_count_items")
        val full_count_items: String
    )
}
