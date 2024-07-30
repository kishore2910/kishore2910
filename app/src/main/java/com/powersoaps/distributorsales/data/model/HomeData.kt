package com.powersoaps.distributorsales.data.model

import android.os.Parcelable
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class HomeData (

    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("data")
    val  data: HomeListData

)
{

    data class HomeListData(

        @field:SerializedName("summary_details")
        val  summary_details: SummaryDetails? = null,

        @field:SerializedName("shop_details")
        val  shop_details: ArrayList<ShopDetails>? = null
    )

    data class SummaryDetails(
        @field:SerializedName("cover_today_value")
        val cover_today_value: Int,

        @field:SerializedName("cover_today_outoff")
        val cover_today_outoff: Int,

        @field:SerializedName("today_order")
        val today_order: Long,

        @field:SerializedName("today_cover")
        val today_cover: Int,

        @field:SerializedName("achived_productivity")
        val achived_productivity: Int,

        @field:SerializedName("overall_productivity")
        val overall_productivity: Int,

        @field:SerializedName("productivity")
        val productivity: Int,

        @field:SerializedName("unit_token")
        val unit_token: String? = null,

        @field:SerializedName("today_collection_amount")
        val today_collection_amount: String? = null,

        @field:SerializedName("overall_item_amount_value")
        val overall_item_amount_value: String? = null

    )

    data class ShopDetails(
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

        @field:SerializedName("units_name")
        val units_name: String
    )
}