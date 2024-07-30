package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class OtpData(

    @field:SerializedName("status_code")
    val statusCode: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String
)
