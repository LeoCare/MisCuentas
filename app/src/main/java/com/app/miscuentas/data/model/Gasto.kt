package com.app.miscuentas.data.model

import android.media.Image
import com.google.gson.annotations.SerializedName

data class Gasto(
    @SerializedName(value = "price") val price: Int,
    @SerializedName(value = "id") val id: String,
    @SerializedName(value = "type") val type: String
)
