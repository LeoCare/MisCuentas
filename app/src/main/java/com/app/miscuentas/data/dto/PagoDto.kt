package com.app.miscuentas.data.dto

import com.google.gson.annotations.SerializedName

data class PagoDto(
    @SerializedName("idPago") val idPago: Long,
    @SerializedName("idParticipantePago") val idParticipantePago: Long,
    @SerializedName("idBalance") val idBalance: Long,
    @SerializedName("idBalancePagado") val idBalancePagado: Long,
    @SerializedName("monto") val monto: Double,
    @SerializedName("idImagen") val idImagen: Long?,
    @SerializedName("fechaPago") val fechaPago: String,
    @SerializedName("fechaConfirmacion") val fechaConfirmacion: String?
)

data class PagoCrearDto(
    @SerializedName("idParticipantePago") val idParticipantePago: Long,
    @SerializedName("idBalance") val idBalance: Long,
    @SerializedName("idBalancePagado") val idBalancePagado: Long,
    @SerializedName("monto") val monto: String,
    @SerializedName("idImagen") val idImagen: Long?,
    @SerializedName("fechaConfirmacion") val fechaConfirmacion: String?,
    @SerializedName("fechaPago") val fechaPago: String

)

