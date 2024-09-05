package com.app.miscuentas.domain.dto

import com.google.gson.annotations.SerializedName

data class HojaDto(
    @SerializedName("idHoja") val idHoja: Long,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String,
    @SerializedName("fechaCierre") val fechaCierre: String?,
    @SerializedName("limiteGastos") val limiteGastos: Double,
    @SerializedName("status") val status: String,
    @SerializedName("idUsuario") val idUsuario: Long
)

data class HojaCrearDto(
    @SerializedName("titulo") val titulo: String,
    @SerializedName("fechaCreacion") val fechaCreacion: String,
    @SerializedName("fechaCierre") val fechaCierre: String?,
    @SerializedName("limiteGastos") val limiteGastos: Double,
    @SerializedName("status") val status: String,
    @SerializedName("idUsuario") val idUsuario: Long
)