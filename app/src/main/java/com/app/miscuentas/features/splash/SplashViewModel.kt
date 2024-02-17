package com.app.miscuentas.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig  // DATASTORE
) : ViewModel() {

    private val _splashState = MutableStateFlow(SplashState())
    val splashState: StateFlow<SplashState> = _splashState

    init {
        // Observa los cambios en DataStore para comprobar el inicio por huella
        viewModelScope.launch {
            val inicioHuella = dataStoreConfig.getInicoHuellaPreference() ?: false
            val registrado = dataStoreConfig.getRegistroPreference() ?: false

            if (registrado && !inicioHuella) _splashState.value = _splashState.value.copy(autoInicio = true)
            else _splashState.value = _splashState.value.copy(autoInicio = false)
        }
    }

    /** PERMISOS **/
    @OptIn(ExperimentalPermissionsApi::class)
//    suspend fun solicitaPermiso(statePermisoCamara: PermissionState) = withContext(Dispatchers.IO) {
//        statePermisoCamara.launchPermissionRequest()
//    }
    fun solicitaPermiso(statePermisoCamara: PermissionState)  {
        statePermisoCamara.launchPermissionRequest()
    }

    fun setPermisoConcedido(){
        _splashState.value = _splashState.value.copy(permisoState = SplashState.PermissionState.Concedido)
    }

    fun setPermisoDenegado(){
        _splashState.value = _splashState.value.copy(permisoState = SplashState.PermissionState.Denegado)
    }

    fun setPermisoDenegPermanente(){
        _splashState.value = _splashState.value.copy(permisoState = SplashState.PermissionState.DenegPermanente)
    }
}
