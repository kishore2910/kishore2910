package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EditOrderData (
    @SerializedName("status_code") @Expose val status_code: Int?,
    @SerializedName("title") @Expose val title: String?,
    @SerializedName("message") @Expose val message: String?,
    @SerializedName("data") @Expose val  data: PreviousOrderList? = null
) {
    data class PreviousOrderList(
        @SerializedName("order_details") @Expose val order_details: ArrayList<OrderProductDetails>? = null,
        @SerializedName("order_val") @Expose val order_val: OrderValues? = null
    )
    {
        data class OrderProductDetails(
            @SerializedName("product_token") @Expose val product_token: String?,
            @SerializedName("product_name") @Expose val product_name: String?,
            @SerializedName("quantity") @Expose val quantity: String?,
            @SerializedName("unit_type") @Expose val units: String?,
            @SerializedName("amount") @Expose val amount: String?,
            @SerializedName("piece_count") @Expose val piece_count: String?,
            @SerializedName("discount_enable") @Expose val discountEnable: Boolean,
            @SerializedName("discount") @Expose val discount : String?,
            @SerializedName("totalamount") @Expose val totalValue: String?,

        )
        data class OrderValues(
            @SerializedName("order_token") @Expose val order_token: String?,
            @SerializedName("total_amount") @Expose val total_amount: String?,
            @SerializedName("quantity") @Expose val quantity: String?
        )
    }
}