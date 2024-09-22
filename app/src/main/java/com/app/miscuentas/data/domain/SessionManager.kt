package com.app.miscuentas.data.domain

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/** ESTA CLASE SERA LA RESPONSABLE DE MANEJAR LOS ESTADOS DE INICIO **/
class SessionManager @Inject constructor(
    private val tokenAuthenticator: TokenAuthenticator,
    private val dataStoreConfig: DataStoreConfig
) {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Authenticated)
    val authState: StateFlow<AuthState> get() = _authState

    suspend fun logout(permitido: Boolean, logeado: String) {
        tokenAuthenticator.clearTokens()
        dataStoreConfig.putInicoHuellaPreference(permitido)
        dataStoreConfig.putRegistroPreference(logeado)
        _authState.value = AuthState.Unauthenticated
    }

    fun notifySessionExpired() {
        _authState.value = AuthState.SessionExpired
    }
}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object SessionExpired : AuthState()
    data class Error(val message: String) : AuthState()
}
