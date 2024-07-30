package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class LocationData(
    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String
)