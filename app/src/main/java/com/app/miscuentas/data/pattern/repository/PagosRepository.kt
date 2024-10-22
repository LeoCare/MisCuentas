package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.data.dto.PagoCrearDto
import com.app.miscuentas.data.dto.PagoDto
import com.app.miscuentas.di.WithInterceptor

class PagosRepository(
    @WithInterceptor
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los pagos
    suspend fun getAllPagos(): List<PagoDto>? {
        return try {
            val response = webService.getAllPagos()
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener pagos: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Obtener un pago por ID
    suspend fun getPagoById(id: Long): PagoDto? {
        return try {
            val response = webService.getPagoById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener pago: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Obtener pagos segun condicion
    suspend fun getPagosBy(column: String, query: String): List<PagoDto>? {
        return try {
            val response = webService.getPagosWhenData(column, query)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener pago: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Crear un nuevo pago
    suspend fun createPago(pagoCrearDto: PagoCrearDto): PagoDto? {
        return try {
            val response = webService.createPago(pagoCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear pago: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            throw Exception("Error de red al crear pago: ${e.message}", e)
        }
    }

    // Actualizar un pago
    suspend fun updatePago(pagoDto: PagoDto): PagoDto? {
        return try {
            val response = webService.updatePago(pagoDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al actualizar pago: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Eliminar un pago por ID
    suspend fun deletePago(id: Long): String? {
        return try {
            val response = webService.deletePago( id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al eliminar pago: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }
}
