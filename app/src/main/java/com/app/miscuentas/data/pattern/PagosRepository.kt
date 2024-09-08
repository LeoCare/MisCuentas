package com.app.miscuentas.data.pattern

import com.app.miscuentas.data.network.PagosService
import com.app.miscuentas.domain.dto.PagoCrearDto
import com.app.miscuentas.domain.dto.PagoDto

class PagosRepository(private val pagoService: PagosService) {

    // Obtener todos los pagos
    suspend fun getAllPagos(token: String): List<PagoDto>? {
        return pagoService.getAllPagos(token)
    }

    // Obtener un pago por ID
    suspend fun getPagoById(token: String, id: Long): PagoDto? {
        return pagoService.getPagoById(token, id)
    }

    // Crear un nuevo pago
    suspend fun createPago(token: String, pagoCrearDto: PagoCrearDto): PagoDto? {
        return pagoService.createPago(token, pagoCrearDto)
    }

    // Actualizar un pago
    suspend fun updatePago(token: String, pagoDto: PagoDto): PagoDto? {
        return pagoService.updatePago(token, pagoDto)
    }

    // Eliminar un pago por ID
    suspend fun deletePago(token: String, id: Long): String? {
        return pagoService.deletePago(token, id)
    }
}
