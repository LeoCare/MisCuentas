package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.network.PagosService
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.PagoCrearDto
import com.app.miscuentas.domain.dto.PagoDto

class PagosRepository(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los pagos
    suspend fun getAllPagos(token: String): List<PagoDto>? {
        return try {
            val response = webService.getAllPagos(token)
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
    suspend fun getPagoById(token: String, id: Long): PagoDto? {
        return try {
            val response = webService.getPagoById(token, id)
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
    suspend fun createPago(token: String, pagoCrearDto: PagoCrearDto): PagoDto? {
        return try {
            val response = webService.createPago(token, pagoCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear pago: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Actualizar un pago
    suspend fun updatePago(token: String, pagoDto: PagoDto): PagoDto? {
        return try {
            val response = webService.updatePago(token, pagoDto)
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
    suspend fun deletePago(token: String, id: Long): String? {
        return try {
            val response = webService.deletePago(token, id)
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
