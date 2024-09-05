package com.app.miscuentas.domain.dto

import com.google.gson.annotations.SerializedName

data class ParticipanteDto(
    @SerializedName("idParticipante") val idParticipante: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("idHoja") val idHoja: Long
)

data class ParticipanteCrearDto(
    @SerializedName("idParticipante") val idParticipante: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("idHoja") val idHoja: Long
)