package com.app.miscuentas.domain.dto

import com.google.gson.annotations.SerializedName

data class PagoDto(
    @SerializedName("idPago") val idPago: Long,
    @SerializedName("idBalance") val idBalance: Long,
    @SerializedName("idBalancePagado") val idBalancePagado: Long,
    @SerializedName("monto") val monto: Double,
    @SerializedName("idImagen") val idImagen: Long?,
    @SerializedName("fechaPago") val fechaPago: String,
    @SerializedName("fechaConfirmacion") val fechaConfirmacion: String?
)

data class PagoCrearDto(
    @SerializedName("idBalance") val idBalance: Long,
    @SerializedName("idBalancePagado") val idBalancePagado: Long,
    @SerializedName("monto") val monto: Double,
    @SerializedName("idImagen") val idImagen: Long?,
    @SerializedName("fechaPago") val fechaPago: String,
    @SerializedName("fechaConfirmacion") val fechaConfirmacion: String?
)

