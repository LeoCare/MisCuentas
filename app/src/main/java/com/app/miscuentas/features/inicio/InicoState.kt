package com.app.miscuentas.features.inicio

data class InicioState(
    val huellaDigital: Boolean = false,
    val permisoState: PermissionState = PermissionState.Denegado
){
    sealed class PermissionState {
        object Concedido : PermissionState()
        object Denegado : PermissionState()
        object DenegPermanente : PermissionState()
    }
}