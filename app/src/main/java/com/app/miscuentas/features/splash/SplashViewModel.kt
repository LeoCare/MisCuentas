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

    fun onContinuarChanged(continuar: Boolean){
        _splashState.value = _splashState.value.copy(continuar = continuar)
    }
    fun onAutoInicioChanged(autoInicio: Boolean){
        _splashState.value = _splashState.value.copy(autoInicio = autoInicio)
    }

    init {
        var inicioHuella: String?
        var registrado: String?
        viewModelScope.launch {
            try {
                 inicioHuella = dataStoreConfig.getInicoHuellaPreference()
                 registrado = dataStoreConfig.getRegistroPreference()

                onContinuarChanged( true)
                if (registrado != null && inicioHuella != "SI") {
                    onAutoInicioChanged( true)
                } else {
                    onAutoInicioChanged(  false)
                }

            }catch (ex: Exception){
                null
            }
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
