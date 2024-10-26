package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.dto.EmailDto
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.di.WithInterceptor

class EmailsRepository (
    @WithInterceptor
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    suspend fun createEmail(emailDto: EmailDto): String? {
        return try {
            val response = webService.createEmail(emailDto)
            if (response.isSuccessful) {
                response.body()?.toString()
            } else {
                throw Exception("Error al insertar el email: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            "NOK"
        }
    }
}