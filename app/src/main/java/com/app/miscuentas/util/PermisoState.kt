package com.app.miscuentas.util

data class PermisoState (
    val permisoState: PermissionState = PermissionState.Denegado
){
    sealed class PermissionState {
        object Concedido : PermissionState()
        object Denegado : PermissionState()
        object DenegPermanente : PermissionState()
    }
}