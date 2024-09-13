package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.network.BalancesService
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.BalanceCrearDto
import com.app.miscuentas.domain.dto.BalanceDto

class BalancesRepository(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los balances
    suspend fun getBalances(token: String): List<BalanceDto>? {
        return try {
            val response = webService.getBalances("Bearer $token")
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener balances: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Obtener un balance por ID
    suspend fun getBalanceById(token: String, id: Long): BalanceDto? {
        return try {
            val response = webService.getBalanceById("Bearer $token", id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener balance: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Crear un nuevo balance
    suspend fun postBalance(token: String, balanceCrearDto: BalanceCrearDto): BalanceDto? {
        return try {
            val response = webService.postBalance("Bearer $token", balanceCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear balance: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar un balance
    suspend fun putBalance(token: String, balanceDto: BalanceDto): BalanceDto? {
        return try {
            val response = webService.putBalance("Bearer $token", balanceDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al actualizar balance: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Eliminar un balance
    suspend fun deleteBalance(token: String, id: Long): String? {
        return try {
            val response = webService.deleteBalance("Bearer $token", id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al eliminar balance: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }
}
