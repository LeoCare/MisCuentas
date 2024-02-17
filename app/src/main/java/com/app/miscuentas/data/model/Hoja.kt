package com.app.miscuentas.data.model

import com.google.gson.annotations.SerializedName

data class Hoja (
    @SerializedName(value = "price") val price: Int,
    @SerializedName(value = "id") val id: String,
    @SerializedName(value = "type") val type: String
    )
