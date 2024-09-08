package com.app.miscuentas.data.network

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.HojaCrearDto
import com.app.miscuentas.domain.dto.HojaDto

class HojasService(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todas las hojas
    suspend fun getAllHojas(token: String): List<HojaDto>? {
        val response = webService.getAllHojas(token)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener hojas: ${response.code()} - ${response.message()}")
        }
    }

    // Obtener una hoja por ID
    suspend fun getHojaById(token: String, id: Long): HojaDto? {
        val response = webService.getHojaById(token, id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener hoja: ${response.code()} - ${response.message()}")
        }
    }

    // Crear una nueva hoja
    suspend fun createHoja(token: String, hojaCrearDto: HojaCrearDto): HojaDto? {
        val response = webService.createHoja(token, hojaCrearDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al crear hoja: ${response.code()} - ${response.message()}")
        }
    }

    // Actualizar una hoja
    suspend fun updateHoja(token: String, hojaDto: HojaDto): HojaDto? {
        val response = webService.updateHoja(token, hojaDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al actualizar hoja: ${response.code()} - ${response.message()}")
        }
    }

    // Eliminar una hoja por ID
    suspend fun deleteHoja(token: String, id: Long): String? {
        val response = webService.deleteHoja(token, id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al eliminar hoja: ${response.code()} - ${response.message()}")
        }
    }
}
