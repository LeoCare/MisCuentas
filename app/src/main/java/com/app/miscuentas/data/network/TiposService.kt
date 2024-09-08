package com.app.miscuentas.data.network

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.dto.TipoBalanceDto
import com.app.miscuentas.data.dto.TipoPerfilDto
import com.app.miscuentas.data.dto.TipoStatusDto
import com.app.miscuentas.data.pattern.webservices.WebService

class TipoPerfilService(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los tipos de perfil
    suspend fun getAllTipoPerfil(token: String): List<TipoPerfilDto>? {
        val response = webService.getAllTipoPerfil(token)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener tipos de perfil: ${response.code()} - ${response.message()}")
        }
    }
}

class TipoBalanceService(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los tipos de balance
    suspend fun getAllTipoBalance(token: String): List<TipoBalanceDto>? {
        val response = webService.getAllTipoBalance(token)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener tipos de balance: ${response.code()} - ${response.message()}")
        }
    }
}

class TipoStatusService(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los tipos de status
    suspend fun getAllTipoStatus(token: String): List<TipoStatusDto>? {
        val response = webService.getAllTipoStatus(token)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener tipos de status: ${response.code()} - ${response.message()}")
        }
    }
}

