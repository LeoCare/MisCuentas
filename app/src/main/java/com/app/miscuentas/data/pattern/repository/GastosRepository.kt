package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.data.dto.GastoCrearDto
import com.app.miscuentas.data.dto.GastoDto
import com.app.miscuentas.di.WithInterceptor

class GastosRepository(
    @WithInterceptor
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los gastos
    suspend fun getAllGastos(): List<GastoDto>? {
        return try {
            val response = webService.getAllGastos()
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
    suspend fun getGastoById(id: Long): GastoDto? {
        return try {
            val response = webService.getGastoById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener gasto: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Obtener todos los gastos
    suspend fun getGastoBy(column: String, query: String): List<GastoDto>? {
        return try {
            val response = webService.getGastosWhenData(column, query)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener gastos: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Crear un nuevo gasto
    suspend fun createGasto(gastoCrearDto: GastoCrearDto): GastoDto? {
        return try {
            val response = webService.createGasto(gastoCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear gasto: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Error en la red al crear gasto: ${e.message}\", e")
        }
    }

    // Actualizar un gasto
    suspend fun updateGasto(gastoDto: GastoDto): GastoDto? {
        return try {
            val response = webService.updateGasto( gastoDto)
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
    suspend fun deleteGasto(id: Long): String? {
        return try {
            val response = webService.deleteGasto( id)
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
