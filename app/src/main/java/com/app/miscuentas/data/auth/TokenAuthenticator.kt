package com.app.miscuentas.data.auth

import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.model.RefreshTokenRequest
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.di.WithoutInterceptor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,
    @WithoutInterceptor
    private val webService: WebService
) {
    // Variable para mantener el token en memoria
    @Volatile
    private var accessTokenCache: String? = null
    @Volatile
    private var refreshTokenCache: String? = null
    private val mutex = Mutex()


    /** Guardar los tokens en DataStore */
    suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStoreConfig.putAccessTokenPreference(accessToken)
        dataStoreConfig.putRefreshTokenPreference(refreshToken)
        accessTokenCache = accessToken
        refreshTokenCache = refreshToken
    }


    /** Obtener el access token desde DataStore */
    suspend fun getAccessToken(): String? {
        accessTokenCache?.let {
            return it
        }
        val token = dataStoreConfig.getAccessTokenPreference()
        accessTokenCache = token
        return token
    }

    /** Obtener el refresh token desde DataStore */
    suspend fun getRefreshToken(): String? {
        refreshTokenCache?.let {
            return it
        }
        val token = dataStoreConfig.getRefreshTokenPreference()
        refreshTokenCache = token
        return token
    }

    /** Limpiar los tokens */
    suspend fun clearTokens() {
        dataStoreConfig.clearAccessTokenPreference()
        dataStoreConfig.clearRefreshTokenPreference()
        accessTokenCache = null
        refreshTokenCache = null
    }

    /** Refrescar el access token usando el refresh token */
    suspend fun refreshAccessToken(): String? {
        return mutex.withLock {
            val refreshToken = getRefreshToken() ?: return null
            try {
                val response = webService.refreshToken(RefreshTokenRequest(refreshToken))
                if (response.isSuccessful) {
                    val newTokens = response.body()
                    if (newTokens != null) {
                        saveTokens(newTokens.accessToken, newTokens.refreshToken)
                        newTokens.accessToken
                    } else {
                        clearTokens()
                        null
                    }
                } else {
                    clearTokens()
                    null
                }
            } catch (e: Exception) {
                clearTokens()
                null
            }
        }
    }
}
