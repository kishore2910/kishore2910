package com.powersoaps.distributorsales.data.model

import com.google.gson.annotations.SerializedName

data class FilterData(
    @field:SerializedName("filterName")
    val filterName: String,

    @field:SerializedName("selected")
    var selected: Boolean
)
