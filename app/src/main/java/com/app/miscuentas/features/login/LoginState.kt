package com.app.miscuentas.features.login

data class LoginState(
    val usuario: String = "",
    val contrasena: String = "",
    val email: String = "",
    val mensaje: String = "",
    val registro: Boolean = false,
    val loginOk: Boolean = false
)
