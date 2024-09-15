package com.app.miscuentas.data.auth

import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val dataStoreConfig: DataStoreConfig
) {
    // Variable para mantener el token en memoria
    @Volatile
    private var tokenCache: String? = null


    /** Guardar el token JWT en DataStore */
    suspend fun saveToken(token: String) {
        dataStoreConfig.putTokenPreference(token)
        tokenCache = token
    }

    /** Obtener el token JWT desde DataStore */
    suspend fun getToken(): String? {
        // Si el token está en el caché, devolverlo
        tokenCache?.let {
            return it
        }
        // Si no está en el caché, obtenerlo de DataStore y actualizar el caché
        val token = dataStoreConfig.getTokenPreference()
        tokenCache = token // Almacenar en caché
        return token
    }

    /** Eliminar el token JWT de DataStore */
    suspend fun clearToken() {
        dataStoreConfig.clearTokenPreference()
        tokenCache = null
    }
}
