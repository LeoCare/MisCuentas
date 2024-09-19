package com.app.miscuentas.data.dto

import com.google.gson.annotations.SerializedName

data class ParticipanteDto(
    @SerializedName("idParticipante") val idParticipante: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("idUsuario") val idUsuario: Long? = null,
    @SerializedName("idHoja") val idHoja: Long
)

data class ParticipanteCrearDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String? = null,
    @SerializedName("idUsuario") val idUsuario: Long? = null,
    @SerializedName("idHoja") val idHoja: Long
)