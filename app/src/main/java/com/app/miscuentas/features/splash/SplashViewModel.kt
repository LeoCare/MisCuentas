package com.app.miscuentas.features.splash

import android.Manifest
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.app.miscuentas.data.domain.AuthState
import com.app.miscuentas.data.domain.SessionManager
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.DATABASE_VERSION
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.data.pattern.DataUpdates
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val dataUpdates: DataUpdates,
    private val dataStoreConfig: DataStoreConfig,
    private val sessionManager: SessionManager,
    private val usuariosService: UsuariosService
) : ViewModel() {

    private val _splashState = MutableStateFlow(SplashState())
    val splashState: StateFlow<SplashState> = _splashState
    val authState: StateFlow<AuthState> = sessionManager.authState

    fun onMensajeChanged(mensaje: String) {
        _splashState.value = _splashState.value.copy(mensaje = mensaje)
    }
    fun onContinuarChanged(continuar: Boolean){
        _splashState.value = _splashState.value.copy(continuar = continuar)
    }
    fun onAutoInicioChanged(autoInicio: Boolean){
        _splashState.value = _splashState.value.copy(autoInicio = autoInicio)
    }
    fun onPermisoTratadoChanged(tratado: Boolean){
        _splashState.value = _splashState.value.copy(permisosTratados = tratado)
    }

    /** Comprueba y limpia datastore si es necesario **/
    fun checkAndClearDataStore() {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val versionActual = DATABASE_VERSION
                val versionGuardada = dataStoreConfig.getDatabaseVersion()

                //Comprobamos si la bbdd ha cambiado y es necesario limpiar las preferences
                checkAndClearDataStore(versionActual, versionGuardada)
            }
        }
    }


    /** METODO PARA COMPROBAR LAS PREFERENCE Y BORRARLAS SI FUERA NECESARIO **/
    suspend fun checkAndClearDataStore(versionActual: Int, versionGuardada: Int?) {
        val inicioHuella: String?
        val registrado: String?

        try {
            //si la version de la bbdd cambia -> limpio las preference
            if (versionGuardada == null || versionActual  > versionGuardada) {
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
            ActualizarDatos()
        }catch (ex: Exception){
            null
        }
    }

    fun ActualizarDatos() {
        viewModelScope.launch {
            val idUsuario = dataStoreConfig.getIdRegistroPreference()
            if (idUsuario != null){
                try {
                    // Actualizar el estado de la UI y cargar Room
                    dataUpdates.limpiarYVolcarLogin(idUsuario)
                    onMensajeChanged("Datos actualizados!")
                    onContinuarChanged(true)
                } catch (e: Exception) {
                    // Si hay un error de red, intentar cargar los datos locales
                    val localUsuario = usuariosService.getRegistroWhereId(idUsuario).firstOrNull()
                    if (localUsuario != null) {
                        onMensajeChanged("Los datos no estan actualizados. Problema de red!")
                        onContinuarChanged(true)
                    } else {
                        onMensajeChanged("Error en la red y no hay datos locales!")
                        clearDataStore()
                        onContinuarChanged(false)
                    }
                }
            }
        }
    }

    /** METODO PARA LIMPIAR LAS PREFERENCES **/
    suspend fun clearDataStore() {
        dataStoreConfig.clearDataStore()
    }
}
