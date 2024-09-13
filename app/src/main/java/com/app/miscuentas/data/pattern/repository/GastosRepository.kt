package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.network.GastosService
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.GastoCrearDto
import com.app.miscuentas.domain.dto.GastoDto

class GastosRepository(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los gastos
    suspend fun getAllGastos(token: String): List<GastoDto>? {
        return try {
            val response = webService.getAllGastos(token)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener gastos: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Obtener un gasto por ID
    suspend fun getGastoById(token: String, id: Long): GastoDto? {
        return try {
            val response = webService.getGastoById(token, id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener gasto: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Crear un nuevo gasto
    suspend fun createGasto(token: String, gastoCrearDto: GastoCrearDto): GastoDto? {
        return try {
            val response = webService.createGasto(token, gastoCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear gasto: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar un gasto
    suspend fun updateGasto(token: String, gastoDto: GastoDto): GastoDto? {
        return try {
            val response = webService.updateGasto(token, gastoDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al actualizar gasto: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Eliminar un gasto por ID
    suspend fun deleteGasto(token: String, id: Long): String? {
        return try {
            val response = webService.deleteGasto(token, id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al eliminar gasto: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

}
