package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class BanksDetailsData(
    @SerializedName("status_code" ) var statusCode : Int?    = null,
    @SerializedName("message"     ) var message    : String? = null,
    @SerializedName("header"      ) var header     : String? = null,
    @SerializedName("data"        ) var data       : Data?   = Data()
)
data class Data (

    @SerializedName("distributor_token" ) var distributorToken : String? = null,
    @SerializedName("bank_name"         ) var bankName         : String? = null,
    @SerializedName("account_number"    ) var accountNumber    : String? = null,
    @SerializedName("bank_code"         ) var bankCode         : String? = null,
    @SerializedName("gpay_number"       ) var gpayNumber       : String? = null,
    @SerializedName("holder_name"       ) var holderName       : String? = null,
    @SerializedName("paytm_number"      ) var paytmNumber      : String? = null

)
