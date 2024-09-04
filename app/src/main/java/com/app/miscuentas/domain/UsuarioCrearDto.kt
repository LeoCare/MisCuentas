package com.app.miscuentas.domain

import com.google.gson.annotations.SerializedName

data class UsuarioCrearDto(
    @SerializedName("contrasenna") val contrasenna: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("perfil") val perfil: String
)