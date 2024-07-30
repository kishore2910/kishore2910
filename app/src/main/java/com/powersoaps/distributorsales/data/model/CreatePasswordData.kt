package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class CreatePasswordData(
    @SerializedName("status_code" ) var statusCode : Int?    = null,
    @SerializedName("message"     ) var message    : String? = null,
    @SerializedName("title"       ) var title      : String? = null
)
