package com.app.miscuentas.data.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName(value = "idUsuario") val idUsuario: Long,
    @SerializedName(value = "nombre") val nombre: String,
    @SerializedName(value = "correo") val correo: String,
    @SerializedName(value = "contrasenna") val contrasenna: String,
    @SerializedName(value = "perfil") val perfil: String
)
