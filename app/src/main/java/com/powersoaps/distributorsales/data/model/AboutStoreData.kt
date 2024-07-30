package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class AboutStoreData(
    @SerializedName("status_code") @Expose val status_code: Int?,
    @SerializedName("title") @Expose val title: String?,
    @SerializedName("message") @Expose val message: String?,
    @SerializedName("data") @Expose val  data: ArrayList<AboutStore>? = null
) {
    data class AboutStore(
        @SerializedName("shop_name") @Expose val shop_name: String? = null,
        @SerializedName("contact_person") @Expose val contact_person: String? = null,
        @SerializedName("join_date") @Expose val join_date: String? = null,
        @SerializedName("mobile_number") @Expose val mobile_number: String? = null,
        @SerializedName("license_number") @Expose val license_number: String? = null,
        @SerializedName("license_image") @Expose val license_image: String? = null,
        @SerializedName("address") @Expose val address: String? = null,
        @SerializedName("city") @Expose val city: String? = null,
        @SerializedName("pincode") @Expose val pincode: String? = null,
        @SerializedName("shop_type") @Expose val shop_type: String? = null,
        @SerializedName("shop_type_code") @Expose val shop_token: String? = null,
        @SerializedName("coordinates") @Expose val coordinates: String? = null,
    )
}