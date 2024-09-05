package com.app.miscuentas.data.network.hoja

import com.app.miscuentas.domain.dto.HojaCrearDto
import com.app.miscuentas.domain.dto.HojaDto

class HojasRepository(
    private val hojaService: HojasService
) {

    // Obtener todas las hojas
    suspend fun getAllHojas(token: String): List<HojaDto>? {
        return hojaService.getAllHojas(token)
    }

    // Obtener una hoja por ID
    suspend fun getHojaById(token: String, id: Long): HojaDto? {
        return hojaService.getHojaById(token, id)
    }

    // Crear una nueva hoja
    suspend fun createHoja(token: String, hojaCrearDto: HojaCrearDto): HojaDto? {
        return hojaService.createHoja(token, hojaCrearDto)
    }

    // Actualizar una hoja
    suspend fun updateHoja(token: String, hojaDto: HojaDto): HojaDto? {
        return hojaService.updateHoja(token, hojaDto)
    }

    // Eliminar una hoja por ID
    suspend fun deleteHoja(token: String, id: Long): String? {
        return hojaService.deleteHoja(token, id)
    }
}
