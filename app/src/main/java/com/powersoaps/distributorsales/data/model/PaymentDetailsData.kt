package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PaymentDetailsData (
    @SerializedName("status_code") @Expose val status_code: Int?,
    @SerializedName("title") @Expose val title: String?,
    @SerializedName("message") @Expose val message: String?,
    @SerializedName("data") @Expose val  data: dataobject? = null
){
    data class dataobject(
        @SerializedName("amout_data") val amout_data : Amout_data,
        @SerializedName("overall_payment_details") val overall_payment_details : ArrayList<Overall_payment_details>
    ) {
        data class Amout_data (
            @SerializedName("billing_amount") val billing_amount : String,
            @SerializedName("paid_amount") val paid_amount : String,
            @SerializedName("outstanding_amount") val outstanding_amount : String
        )
        data class Overall_payment_details (
            @SerializedName("date_time") val date_time : String,
            @SerializedName("payment") val payment : String,
            @SerializedName("amount") val amount : String,
            @SerializedName("payment_mode") val payment_mode : String
        )
    }

}