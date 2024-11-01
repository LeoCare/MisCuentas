package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.di.WithInterceptor
import com.app.miscuentas.data.dto.BalanceCrearDto
import com.app.miscuentas.data.dto.BalanceDto

class BalancesRepository(
    @WithInterceptor
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los balances
    suspend fun getBalances(): List<BalanceDto>? {
        return try {
            val response = webService.getBalances()
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
    suspend fun getBalanceById(id: Long): BalanceDto? {
        return try {
            val response = webService.getBalanceById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener balance: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Obtener balances segun coincidan con la consulta
    suspend fun getBalanceBy(column: String, query: String): List<BalanceDto>? {
        return try {
            val response = webService.getBalanceWhenData(column, query)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener balances: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Crear un nuevo balance
    suspend fun postBalance(balanceCrearDto: BalanceCrearDto): BalanceDto? {
        return try {
            val response = webService.postBalance(balanceCrearDto)
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
    suspend fun putBalance(balanceDto: BalanceDto): BalanceDto? {
        return try {
            val response = webService.putBalance(balanceDto)
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
    suspend fun deleteBalance(id: Long): String? {
        return try {
            val response = webService.deleteBalance(id)
            if (response.isSuccessful) {
                response.body()?.toString()
            } else {
                throw Exception("Error al eliminar balance: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }
}
