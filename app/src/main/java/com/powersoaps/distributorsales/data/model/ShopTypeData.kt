package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class ShopTypeData (

    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("data")
    val  data: ArrayList<ShopListData>? = null
)
{
    data class ShopListData(
        @field:SerializedName("shop_code")
        val shop_code: String,

        @field:SerializedName("shop_type_name")
        val shop_type_name: String
    )

}