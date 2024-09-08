package com.app.miscuentas.data.network

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.GastoCrearDto
import com.app.miscuentas.domain.dto.GastoDto

class GastosService(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los gastos
    suspend fun getAllGastos(token: String): List<GastoDto>? {
        val response = webService.getAllGastos(token)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener gastos: ${response.code()} - ${response.message()}")
        }
    }

    // Obtener un gasto por ID
    suspend fun getGastoById(token: String, id: Long): GastoDto? {
        val response = webService.getGastoById(token, id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener gasto: ${response.code()} - ${response.message()}")
        }
    }

    // Crear un nuevo gasto
    suspend fun createGasto(token: String, gastoCrearDto: GastoCrearDto): GastoDto? {
        val response = webService.createGasto(token, gastoCrearDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al crear gasto: ${response.code()} - ${response.message()}")
        }
    }

    // Actualizar un gasto
    suspend fun updateGasto(token: String, gastoDto: GastoDto): GastoDto? {
        val response = webService.updateGasto(token, gastoDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al actualizar gasto: ${response.code()} - ${response.message()}")
        }
    }

    // Eliminar un gasto por ID
    suspend fun deleteGasto(token: String, id: Long): String? {
        val response = webService.deleteGasto(token, id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al eliminar gasto: ${response.code()} - ${response.message()}")
        }
    }
}
