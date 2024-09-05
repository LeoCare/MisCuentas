package com.app.miscuentas.data.network.gasto

import com.app.miscuentas.domain.dto.GastoCrearDto
import com.app.miscuentas.domain.dto.GastoDto

class GastosRepository(
    private val gastoService: GastosService
) {

    // Obtener todos los gastos
    suspend fun getAllGastos(token: String): List<GastoDto>? {
        return gastoService.getAllGastos(token)
    }

    // Obtener un gasto por ID
    suspend fun getGastoById(token: String, id: Long): GastoDto? {
        return gastoService.getGastoById(token, id)
    }

    // Crear un nuevo gasto
    suspend fun createGasto(token: String, gastoCrearDto: GastoCrearDto): GastoDto? {
        return gastoService.createGasto(token, gastoCrearDto)
    }

    // Actualizar un gasto
    suspend fun updateGasto(token: String, gastoDto: GastoDto): GastoDto? {
        return gastoService.updateGasto(token, gastoDto)
    }

    // Eliminar un gasto por ID
    suspend fun deleteGasto(token: String, id: Long): String? {
        return gastoService.deleteGasto(token, id)
    }
}
