package com.app.miscuentas.data.network

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.BalanceCrearDto
import com.app.miscuentas.domain.dto.BalanceDto

class BalancesService(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los balances
    suspend fun getBalances(token: String): List<BalanceDto>? {
        val response = webService.getBalances("Bearer $token")
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener balances: ${response.code()} - ${response.message()}")
        }
    }

    // Obtener un balance por ID
    suspend fun getBalanceById(token: String, id: Long): BalanceDto? {
        val response = webService.getBalanceById("Bearer $token", id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener balance: ${response.code()} - ${response.message()}")
        }
    }

    // Crear un nuevo balance
    suspend fun postBalance(token: String, balanceCrearDto: BalanceCrearDto): BalanceDto? {
        val response = webService.postBalance("Bearer $token", balanceCrearDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al crear balance: ${response.code()} - ${response.message()}")
        }
    }

    // Actualizar un balance
    suspend fun putBalance(token: String, balanceDto: BalanceDto): BalanceDto? {
        val response = webService.putBalance("Bearer $token", balanceDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al actualizar balance: ${response.code()} - ${response.message()}")
        }
    }

    // Eliminar un balance
    suspend fun deleteBalance(token: String, id: Long): String? {
        val response = webService.deleteBalance("Bearer $token", id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al eliminar balance: ${response.code()} - ${response.message()}")
        }
    }
}