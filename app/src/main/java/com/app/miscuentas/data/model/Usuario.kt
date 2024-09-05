package com.app.miscuentas.data.model

data class Usuario(
    val contrasenna: String,
    val correo: String,
    val idUsuario: Long = 0,
    val nombre: String,
    val perfil: String
)