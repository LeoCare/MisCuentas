package com.app.miscuentas.domain

import com.app.miscuentas.data.pattern.PagosRepository
import com.app.miscuentas.domain.dto.PagoCrearDto
import com.app.miscuentas.domain.dto.PagoDto

class GetPagos(
    private val pagosRepository: PagosRepository
){

    // Obtener todos los pagos
    suspend fun getAllPagos(token: String): List<PagoDto>? {
        return pagosRepository.getAllPagos(token)
    }

    // Obtener un pago por ID
    suspend fun getPagoById(token: String, id: Long): PagoDto? {
        return pagosRepository.getPagoById(token, id)
    }

    // Crear un nuevo pago
    suspend fun createPago(token: String, pagoCrearDto: PagoCrearDto): PagoDto? {
        return pagosRepository.createPago(token, pagoCrearDto)
    }

    // Actualizar un pago
    suspend fun updatePago(token: String, pagoDto: PagoDto): PagoDto? {
        return pagosRepository.updatePago(token, pagoDto)
    }

    // Eliminar un pago por ID
    suspend fun deletePago(token: String, id: Long): String? {
        return pagosRepository.deletePago(token, id)
    }
}