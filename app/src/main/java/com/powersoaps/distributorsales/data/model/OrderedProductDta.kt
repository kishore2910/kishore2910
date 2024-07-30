package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderedProductDta(
        @SerializedName("category_token") @Expose val category_token: String?,
        @SerializedName("category_name") @Expose val category_name: String?,
        @SerializedName("token") @Expose val token: String?,
        @SerializedName("name") @Expose val name: String?,
        @SerializedName("item_code") @Expose val item_code: String?,
        @SerializedName("product_per_price") @Expose val product_per_price: String?,
        @SerializedName("product_gst") @Expose val product_gst: String?,
        @SerializedName("order_count") @Expose val order_count: String?,
        @SerializedName("order_type") @Expose val order_type: String?,
        @SerializedName("piece_count") @Expose val piece_count: String?,
        @SerializedName("discount_enable") @Expose var discount_enable: Boolean =false,
        @SerializedName("percentage") @Expose var percentage: String? = "0%",
        @SerializedName("is_scheme") @Expose var IsAdditional_Available: Boolean?,
        @SerializedName("limit_box") @Expose var BoxMaxCountOffer: Int?,
        @SerializedName("free_box") @Expose var BoxMinCountOffer: Int?,
        @SerializedName("is_free") @Expose var is_free: Boolean?
    )
