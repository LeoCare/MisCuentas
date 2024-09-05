package com.app.miscuentas.data.network.balance

import com.app.miscuentas.domain.dto.BalanceCrearDto
import com.app.miscuentas.domain.dto.BalanceDto

class BalancesRepository(
    private val balanceService: BalancesService
) {

    // Obtener todos los balances
    suspend fun getBalances(token: String): List<BalanceDto>? {
        return balanceService.getBalances(token)
    }

    // Obtener un balance por ID
    suspend fun getBalanceById(token: String, id: Long): BalanceDto? {
        return balanceService.getBalanceById(token, id)
    }

    // Crear un nuevo balance
    suspend fun postBalance(token: String, balanceCrearDto: BalanceCrearDto): BalanceDto? {
        return balanceService.postBalance(token, balanceCrearDto)
    }

    // Actualizar un balance
    suspend fun putBalance(token: String, balanceDto: BalanceDto): BalanceDto? {
        return balanceService.putBalance(token, balanceDto)
    }

    // Eliminar un balance por ID
    suspend fun deleteBalance(token: String, id: Long): String? {
        return balanceService.deleteBalance(token, id)
    }
}
