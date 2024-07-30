package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class OrderDetailsData(
    @SerializedName("status_code") @Expose val status_code: Int?,
    @SerializedName("title") @Expose val title: String?,
    @SerializedName("message") @Expose val message: String?,
    @SerializedName("data") @Expose val  data: OrderProductList? = null
) {
    data class OrderProductList(
        @SerializedName("order_details") @Expose val order_details: ArrayList<OrderProductData>? = null,
        @SerializedName("order_val") @Expose val order_val: OrderDataValues? = null
    )
    {
        data class OrderProductData(
            @SerializedName("product_token") @Expose val product_token: String?,
            @SerializedName("quantity") @Expose val quantity: String?,
            @SerializedName("units") @Expose val units: String?,
            @SerializedName("misc_price") @Expose val misc_price: String?,
            @SerializedName("product_name") @Expose val product_name: String?,
            @SerializedName("percentage_value") @Expose val percentage_value: String?,
            @SerializedName("amount") @Expose val amount: String?,
            @SerializedName("is_discount_enable") @Expose val is_discount_enable: String?,
            @SerializedName("is_free") @Expose val is_free: Boolean=false
            )
        data class OrderDataValues(
            @SerializedName("gst") @Expose val gst: String?,
            @SerializedName("pro_amount") @Expose val pro_amount: String?,
            @SerializedName("total_amount") @Expose val total_amount: String?,
            @SerializedName("order_delivery") @Expose val order_delivery: String?,
            @SerializedName("discount_amount_finall") @Expose val discount_amount_finall: String?,
            @SerializedName("offer_value") @Expose val offer_value: String?,
            @SerializedName("order_schedule_date") @Expose val order_schedule_date: String?,
            @SerializedName("order_delivery_date") @Expose val order_delivery_date: String?,
            @SerializedName("order_token") @Expose val order_token: String?,
            @SerializedName("order_items") @Expose val order_items: String?,
            @SerializedName("shop_name") @Expose val shop_name: String?,
            @SerializedName("shop_token") @Expose val shop_token: String?,
            @SerializedName("order_status") @Expose val order_status: String?,
            @SerializedName("employee_name") @Expose val employee_name: String?,
            @SerializedName("over_all_amount") @Expose val over_all_amount: String?,
            @SerializedName("discount_new_value") @Expose val discount_new_value: String?,
            @SerializedName("percentage_offer_value") @Expose val percentage_offer_value: Int?
        )
    }
}