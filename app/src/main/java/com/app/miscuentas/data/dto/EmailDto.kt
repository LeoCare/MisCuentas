package com.app.miscuentas.data.dto

import com.google.gson.annotations.SerializedName

data class EmailDto(
    @SerializedName("idBalance") val idBalance: Long,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("fechaEnvio") val fechaEnvio: String?,
    @SerializedName("status") val status: String
)
