package com.app.miscuentas.data.auth

import okhttp3.Interceptor
import okhttp3.Response

class JwtInterceptor(private val tokenAuthenticator: TokenAuthenticator) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Si el token existe, añadirlo al header
        tokenAuthenticator.getToken()?.let { token ->
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}
