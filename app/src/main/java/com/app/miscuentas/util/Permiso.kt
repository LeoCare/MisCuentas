package com.app.miscuentas.util

import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Permiso {

    private val _permisoState = MutableStateFlow(PermisoState())
    val permisoState: StateFlow<PermisoState> = _permisoState


    fun setPermisoConcedido(){
        _permisoState.value = _permisoState.value.copy(permisoState = PermisoState.PermissionState.Concedido)
    }

    fun setPermisoDenegado(){
        _permisoState.value = _permisoState.value.copy(permisoState = PermisoState.PermissionState.Denegado)
    }

    fun setPermisoDenegPermanente(){
        _permisoState.value = _permisoState.value.copy(permisoState = PermisoState.PermissionState.DenegPermanente)
    }


    @OptIn(ExperimentalPermissionsApi::class)
    fun solicitarPermiso(statePermisoCamara: PermissionState){
        statePermisoCamara.launchPermissionRequest()
    }


}