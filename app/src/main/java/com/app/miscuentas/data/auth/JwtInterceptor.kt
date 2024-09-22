package com.app.miscuentas.data.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Base64
import com.app.miscuentas.data.domain.SessionManager
import org.json.JSONObject
import java.io.IOException

/** LOGICA QUE RECUPERA, VERIFICA Y AGREGA EL TOKEN EN TODAS LAS LLAMADAS A LOS SERVICIOS DE LA API **/
class JwtInterceptor(
    private val tokenAuthenticator: TokenAuthenticator,
    private val sessionManager: SessionManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val requestBuilder = request.newBuilder()

        val accessToken = runBlocking {
            tokenAuthenticator.getAccessToken()
        }

        if (accessToken != null && !isTokenExpired(accessToken)) {
            requestBuilder.addHeader("Authorization", "Bearer $accessToken")
            request = requestBuilder.build()
            var response = chain.proceed(request)
            if (response.code == 401) {
                // El token puede haber expirado justo ahora, intentamos renovar
                response.close()
                val newAccessToken = runBlocking {
                    tokenAuthenticator.refreshAccessToken()
                }
                if (newAccessToken != null) {
                    // Reintentamos la solicitud con el nuevo access token
                    val newRequest = request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                    response = chain.proceed(newRequest)
                } else {
                    // No se pudo refrescar el token, limpiar tokens y manejar según sea necesario
                    runBlocking {
                        tokenAuthenticator.clearTokens()
                    }
                    // Redirigir al usuario al Splash
                    sessionManager.notifySessionExpired()
                }
            }
            return response
        } else {
            // El token es nulo o ha expirado, intentamos renovar
            val newAccessToken = runBlocking {
                tokenAuthenticator.refreshAccessToken()
            }
            if (newAccessToken != null) {
                // Reintentamos la solicitud con el nuevo access token
                val newRequest = requestBuilder
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
                return chain.proceed(newRequest)
            } else {
                // No se pudo refrescar el token, proceder sin token o manejar según sea necesario
                return chain.proceed(requestBuilder.build())
            }
        }
    }

    fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true

            val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
            val json = JSONObject(payload)
            val exp = json.getLong("exp")

            val now = System.currentTimeMillis() / 1000
            exp < now
        } catch (e: Exception) {
            true // Si hay un error al decodificar, asumimos que el token ha expirado
        }
    }
}

