package com.app.miscuentas.features.splash

import com.app.miscuentas.data.domain.AuthState

data class SplashState(
    val permisosTratados: Boolean = false,
    val continuar: Boolean = false,
    val autoInicio: Boolean = false,
    val mensaje: String = "",
)

