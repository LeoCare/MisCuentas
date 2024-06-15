package com.app.miscuentas.features.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.DATABASE_VERSION
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
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val versionActual = DATABASE_VERSION
                val versionGuardada = dataStoreConfig.getDatabaseVersion()

                checkAndClearDataStore(versionActual, versionGuardada)
            }
        }
    }

    suspend fun checkAndClearDataStore(versionActual: Int, versionGuardada: Int?) {
        val inicioHuella: String?
        val registrado: String?

        try {//si la version de la bbdd cambia -> limpio las preference
            if (versionGuardada == null || versionGuardada < versionActual) {
                // Limpiar DataStore
                clearDataStore()
                // Actualizar la versión de la base de datos en DataStore
                dataStoreConfig.saveDatabaseVersion(versionActual)
            }
            else {//si no, las recoge y actua en consecuencia
                inicioHuella = dataStoreConfig.getInicoHuellaPreference()
                registrado = dataStoreConfig.getRegistroPreference()

                if (registrado != null && inicioHuella != "SI") {
                    onAutoInicioChanged(true)
                } else {
                    onAutoInicioChanged(false)
                }
            }
            onContinuarChanged(true)
        }catch (ex: Exception){
            null
        }
    }

    suspend fun clearDataStore() {
        dataStoreConfig.clearDataStore()
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
