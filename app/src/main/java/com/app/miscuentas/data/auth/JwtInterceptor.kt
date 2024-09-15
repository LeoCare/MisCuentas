package com.app.miscuentas.data.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import android.util.Base64
import org.json.JSONObject
import java.io.IOException

/** LOGICA QUE RECUPERA, VERIFICA Y AGREGA EL TOKEN EN TODAS LAS LLAMADAS A LOS SERVICIOS DE LA API **/
class JwtInterceptor(
    private val tokenAuthenticator: TokenAuthenticator
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Para asegurarme que tengo el token cargado, lo obtengo de forma síncrona desde el caché
        // Crear una corrutina en un contexto específico que no bloquee el hilo
        // Obtener el token y verificar si ha expirado
        val token = runBlocking {
            tokenAuthenticator.getToken()
        }

        // Verificar si el token es válido
        if (token != null && !isTokenExpired(token)) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        } else {
            // El token es nulo o ha expirado
            runBlocking {
                tokenAuthenticator.clearToken() // Limpiar el token guardado
            }
            // Aquí puedes lanzar una excepción o redirigir al usuario de alguna forma
            //throw TokenExpiredException("La sesión ha expirado. Por favor, inicia sesión nuevamente.")
        }

        return chain.proceed(requestBuilder.build())
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

