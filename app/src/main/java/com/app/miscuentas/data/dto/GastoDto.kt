package com.app.miscuentas.data.dto

import com.google.gson.annotations.SerializedName

data class GastoDto(
    @SerializedName("idGasto") val idGasto: Long,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("concepto") val concepto: String,
    @SerializedName("importe") val importe: Double,
    @SerializedName("fechaGasto") val fechaGasto: String,
    @SerializedName("idParticipante") val idParticipante: Long,
    @SerializedName("idImagen") val idImagen: Long?
)

data class GastoCrearDto(
    @SerializedName("tipo") val tipo: String,
    @SerializedName("concepto") val concepto: String,
    @SerializedName("importe") val importe: String,
    @SerializedName("fechaGasto") val fechaGasto: String,
    @SerializedName("idParticipante") val idParticipante: Long,
    @SerializedName("idImagen") val idImagen: Long?
)