package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class UserData (

    @field:SerializedName("status_code")
    val status_code: Int,

    @field:SerializedName("message")
    val message: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("data")
    val  data: UserDetails? = null
){
    data class UserDetails(
        @field:SerializedName("id")
        val id: String,

        @field:SerializedName("token")
        val token: String,

        @field:SerializedName("name")
        val name: String,

        @field:SerializedName("employees_code")
        val employees_code: String,

        @field:SerializedName("deparment_token")
        val deparment_token: String,

        @field:SerializedName("gender")
        val gender: String,

        @field:SerializedName("mobile_number")
        val mobile_number: String,

        @field:SerializedName("region")
        val region: String? = null,

        @field:SerializedName("dep_name")
        val dep_name: String

    )

}