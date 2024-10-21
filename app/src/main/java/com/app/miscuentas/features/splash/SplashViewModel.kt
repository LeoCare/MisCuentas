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
import kotlinx.coroutines.flow.update
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
    fun onIsRefreshingChanged(refrescar: Boolean){
        _splashState.update { currentState ->
            currentState.copy(isRefreshing = refrescar)
        }
    }
    /** Comprueba y limpia datastore si es necesario **/
    fun checkAndClearDataStore() {
        onIsRefreshingChanged(true)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val versionActual = DATABASE_VERSION
                    val versionGuardada = withContext(Dispatchers.IO) { dataStoreConfig.getDatabaseVersion() }

                    if (versionGuardada == null || versionActual > versionGuardada) {
                        limpiarDataStore(versionActual)
                    } else {
                        cargarPreferencias()
                    }
                    actualizarDatos()
                }
                catch (e: Exception) {
                    onMensajeChanged("Error al cargar los datos.") //algo a fallado en las solicitudes
                }
            }
        }

    }

    /** Elimina lod datos de la DataStorePreferences **/
    private suspend fun limpiarDataStore(versionActual: Int) {
        withContext(Dispatchers.IO) {
            clearDataStore()
            dataStoreConfig.saveDatabaseVersion(versionActual)
        }
    }

    /** Carga los datos de la DataStorePreferences **/
    private suspend fun cargarPreferencias() {
        val inicioHuella = dataStoreConfig.getInicoHuellaPreference()
        val registrado = dataStoreConfig.getRegistroPreference()
        if (registrado != null && inicioHuella != "SI") {
            onAutoInicioChanged(true)
        } else {
            onAutoInicioChanged(false)
        }
    }

    /** Comienzo de la actualizacion de todos los datos del usuario registrado **/
    private fun actualizarDatos() {
        viewModelScope.launch {
            val idUsuario = dataStoreConfig.getIdRegistroPreference()
            if (idUsuario != null) {
                try {
                    withContext(Dispatchers.IO) {
                        dataUpdates.limpiarYVolcarLogin(idUsuario)
                    }
                    onMensajeChanged("Datos actualizados!")
                    onContinuarChanged(true)
                } catch (e: Exception) {
                    manejarErrorActualizacionDatos(idUsuario, e)
                }
            } else {
                onContinuarChanged(true)
            }
        }
    }

    /** Obtiene el id del registrado si hay algun error de red y deja continuar **/
    private suspend fun manejarErrorActualizacionDatos(idUsuario: Long, e: Exception) {
        val localUsuario = usuariosService.getRegistroWhereId(idUsuario).firstOrNull()
        if (localUsuario != null) {
            onMensajeChanged("Los datos no están actualizados. Problema de red!")
            onContinuarChanged(true)
        } else {
            onMensajeChanged("Error en la red y no hay datos locales!")
            clearDataStore()
            onContinuarChanged(false)
        }
    }

    /** METODO PARA LIMPIAR LAS PREFERENCES **/
    suspend fun clearDataStore() {
        dataStoreConfig.clearDataStore()
    }
}
