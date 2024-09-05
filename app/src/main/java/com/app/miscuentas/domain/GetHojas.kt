package com.app.miscuentas.domain

import com.app.miscuentas.data.network.hoja.HojasRepository
import com.app.miscuentas.data.network.hoja.HojasService
import com.app.miscuentas.domain.dto.HojaCrearDto
import com.app.miscuentas.domain.dto.HojaDto

class GetHojas(
    private val hojasRepository: HojasRepository
) {

    // Obtener todas las hojas
    suspend fun getAllHojas(token: String): List<HojaDto>? {
        return hojasRepository.getAllHojas(token)
    }

    // Obtener una hoja por ID
    suspend fun getHojaById(token: String, id: Long): HojaDto? {
        return hojasRepository.getHojaById(token, id)
    }

    // Crear una nueva hoja
    suspend fun createHoja(token: String, hojaCrearDto: HojaCrearDto): HojaDto? {
        return hojasRepository.createHoja(token, hojaCrearDto)
    }

    // Actualizar una hoja
    suspend fun updateHoja(token: String, hojaDto: HojaDto): HojaDto? {
        return hojasRepository.updateHoja(token, hojaDto)
    }

    // Eliminar una hoja por ID
    suspend fun deleteHoja(token: String, id: Long): String? {
        return hojasRepository.deleteHoja(token, id)
    }
}