package com.app.miscuentas.features.splash

data class SplashState(
    val autoInicio: Boolean = false,
    val permisoState: PermissionState? = null
){
    sealed class PermissionState {
        object Concedido : PermissionState()
        object Denegado : PermissionState()
        object DenegPermanente : PermissionState()
    }
}
