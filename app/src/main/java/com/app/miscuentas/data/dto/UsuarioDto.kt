package com.app.miscuentas.domain.dto

import com.google.gson.annotations.SerializedName

data class UsuarioCrearDto(
    @SerializedName("contrasenna") val contrasenna: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("perfil") val perfil: String
)

data class UsuarioDto(
    @SerializedName("idUsuario") val idUsuario: Long,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasenna") val contrasenna: String,
    @SerializedName("perfil") val perfil: String
)

data class UsuarioLoginDto(
    @SerializedName("correo") val correo: String,
    @SerializedName("contrasenna") val contrasenna: String
)

data class UsuarioWithTokenDto(
    @SerializedName("usuario") val usuario: UsuarioDto,
    @SerializedName("token") val token: String
)

data class UsuarioDeleteDto(
    @SerializedName("nombre") val nombre: String,
    @SerializedName("correo") val correo: String
)