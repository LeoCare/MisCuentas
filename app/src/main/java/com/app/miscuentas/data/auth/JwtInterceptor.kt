package com.app.miscuentas.data.auth

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(private val tokenAuthenticator: TokenAuthenticator) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Para asegurarme que tengo el token cargado, lo obtengo de forma síncrona desde el caché
        runBlocking {
            // Si el token existe, añadirlo al header
            tokenAuthenticator.getToken()?.let { token ->
                requestBuilder.addHeader("Authorization", "Bearer $token")
            }
        }

        return chain.proceed(requestBuilder.build())
    }
}
