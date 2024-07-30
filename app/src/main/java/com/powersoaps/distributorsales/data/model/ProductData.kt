package com.powersoaps.distributorsales.data.model

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductData (
    @SerializedName("status_code") @Expose val status_code: Int?,
    @SerializedName("title") @Expose val title: String?,
    @SerializedName("message") @Expose val message: String?,
    @SerializedName("data") @Expose val  data: ArrayList<ProductListData>? = null
        )   {
    data class ProductListData(
        @SerializedName("category_token") @Expose val category_token: String?,
        @SerializedName("category_name") @Expose val category_name: String?,
        @SerializedName("token") @Expose val token: String?,
        @SerializedName("name") @Expose val name: String?,
        @SerializedName("item_code") @Expose val item_code: String?,
        @SerializedName("product_per_price") @Expose val total_cost: String?,
        @SerializedName("product_gst") @Expose val product_gst: String?,
        @SerializedName("net_weight") @Expose val net_weight: String?,
        @SerializedName("added_count") @Expose var added_count: String?= null,
        @SerializedName("added_type") @Expose var added_type: String?= null,
        @SerializedName("piece_count") @Expose val piece_count: String?,
        @SerializedName("stock_in_hand") @Expose val stock_in_hand: String?,
        @SerializedName("additional_offer") @Expose var additional_offer: String?=null,
        @SerializedName("discount_enable") @Expose var discount_enable: Boolean=false,
        @SerializedName("percentage") @Expose var percentage: Double? = 0.0,
        @SerializedName("total_value") @Expose var total_value: Double = 0.0,
        @SerializedName("is_scheme") @Expose var IsAdditional_Available: Boolean?,
        @SerializedName("limit_box") @Expose var BoxMaxCountOffer: Int?,
        @SerializedName("free_box") @Expose var BoxMinCountOffer: Int?,
        @SerializedName("tag") @Expose var tag: String?=null,
        @SerializedName("is_free") @Expose var is_free: Boolean=false,
        @SerializedName("image") @Expose val image: String?

    )
}