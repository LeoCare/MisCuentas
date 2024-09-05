package com.app.miscuentas.domain


import com.app.miscuentas.data.network.gasto.GastosRepository
import com.app.miscuentas.domain.dto.GastoCrearDto
import com.app.miscuentas.domain.dto.GastoDto

class GetGastos(
    private val gastosRepository: GastosRepository
){

    // Obtener todos los gastos
    suspend fun getAllGastos(token: String): List<GastoDto>? {
        return gastosRepository.getAllGastos(token)
    }

    // Obtener un gasto por ID
    suspend fun getGastoById(token: String, id: Long): GastoDto? {
        return gastosRepository.getGastoById(token, id)
    }

    // Crear un nuevo gasto
    suspend fun createGasto(token: String, gastoCrearDto: GastoCrearDto): GastoDto? {
        return gastosRepository.createGasto(token, gastoCrearDto)
    }

    // Actualizar un gasto
    suspend fun updateGasto(token: String, gastoDto: GastoDto): GastoDto? {
        return gastosRepository.updateGasto(token, gastoDto)
    }

    // Eliminar un gasto por ID
    suspend fun deleteGasto(token: String, id: Long): String? {
        return gastosRepository.deleteGasto(token, id)
    }
}