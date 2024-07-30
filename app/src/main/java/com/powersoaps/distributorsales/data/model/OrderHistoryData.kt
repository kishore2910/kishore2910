package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class OrderHistoryData(

    @SerializedName("status_code") @Expose val status_code: Int?,
    @SerializedName("title") @Expose val title: String?,
    @SerializedName("message") @Expose val message: String?,
    @SerializedName("data") @Expose val  data: OrderHistoryObjects? = null
) {
    data class OrderHistoryObjects(
        @SerializedName("outstanding_value") @Expose val  outstanding_value: Outstanding_value? = null,
        @SerializedName("order_hisyory_data") @Expose val  order_hisyory_data: ArrayList<OrderHistoryList>? = null,
        )
    {
        data class Outstanding_value(
            @SerializedName("outstanding_amount_value") @Expose val outstanding_amount_value: String?
        )
        data class OrderHistoryList(
            @SerializedName("schedule_date") @Expose val schedule_date: String?,
            @SerializedName("order_token") @Expose val order_token: String?,
            @SerializedName("shop_token") @Expose val shop_token: String?,
            @SerializedName("shop_name") @Expose val shop_name: String?,
            @SerializedName("status") @Expose val status: String?,
            @SerializedName("billing_amount") @Expose val billing_amount: String?,
            @SerializedName("paid_amount") @Expose val paid_amount: String?,
            @SerializedName("outstanding_amt") @Expose val outstanding_amt: String?,
            @SerializedName("items") @Expose val items: String?
        )
    }
}