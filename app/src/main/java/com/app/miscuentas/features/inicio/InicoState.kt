package com.app.miscuentas.features.inicio

data class InicioState(
    val huellaDigital: Boolean = false,
    val registrado: String = "",
    val idHojaPrincipal: Long = 0
)