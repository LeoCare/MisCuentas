package com.app.miscuentas.domain.dto

import com.google.gson.annotations.SerializedName

data class BalanceDto(
    @SerializedName("idBalance") val idBalance: Long,
    @SerializedName("idHoja") val idHoja: Long,
    @SerializedName("idParticipante") val idParticipante: Long,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("monto") val monto: String
)

data class BalanceCrearDto(
    @SerializedName("idHoja") val idHoja: Long,
    @SerializedName("idParticipante") val idParticipante: Long,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("monto") val monto: String
)