package com.app.miscuentas.domain

import com.app.miscuentas.data.pattern.BalancesRepository
import com.app.miscuentas.domain.dto.BalanceCrearDto
import com.app.miscuentas.domain.dto.BalanceDto

class GetBalances (
    private val balanceRepository: BalancesRepository
){

    // Obtener todos los balances
    suspend fun getBalances(token: String): List<BalanceDto>? {
        return balanceRepository.getBalances(token)
    }

    // Obtener un balance por ID
    suspend fun getBalanceById(token: String, id: Long): BalanceDto? {
        return balanceRepository.getBalanceById(token, id)
    }

    // Crear un nuevo balance
    suspend fun postBalance(token: String, balanceCrearDto: BalanceCrearDto): BalanceDto? {
        return balanceRepository.postBalance(token, balanceCrearDto)
    }

    // Actualizar un balance
    suspend fun putBalance(token: String, balanceDto: BalanceDto): BalanceDto? {
        return balanceRepository.putBalance(token, balanceDto)
    }

    // Eliminar un balance por ID
    suspend fun deleteBalance(token: String, id: Long): String? {
        return balanceRepository.deleteBalance(token, id)
    }
}