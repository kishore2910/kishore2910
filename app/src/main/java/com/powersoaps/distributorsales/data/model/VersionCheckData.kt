package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VersionCheckData (
    @SerializedName("status_code") @Expose val status_code: Int?,
    @SerializedName("isForceUpdate") @Expose val isForceUpdate: Boolean?,
    @SerializedName("app_link") @Expose val app_link: String?,
    @SerializedName("title") @Expose val title: String?,
    @SerializedName("description") @Expose val description: String?,
    @SerializedName("button_title") @Expose val button_title: String?
)