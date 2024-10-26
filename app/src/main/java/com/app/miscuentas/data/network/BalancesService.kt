package com.app.miscuentas.data.network

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Transaction
import com.app.miscuentas.data.local.dbroom.entitys.DbBalancesEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.pattern.dao.DbBalanceDao
import com.app.miscuentas.data.pattern.repository.BalancesRepository
import com.app.miscuentas.data.dto.BalanceCrearDto
import com.app.miscuentas.data.dto.BalanceDto

class BalancesService(
    private val balanceDao: DbBalanceDao,
    private val balancesRepository: BalancesRepository
) {
    /*****************/
    /** ROOM (local)**/
    /*****************/
    @Transaction
    @Insert
    suspend fun insertBalance(balance: DbBalancesEntity): Long {
        return balanceDao.insertBalance(balance)
    }

    @Transaction
    suspend fun getBalanceByHoja(idHoja: Long): List<DbBalancesEntity> {
        return balanceDao.getBalanceByHoja(idHoja)
    }

    @Transaction
    suspend fun getBalanceById(idBalance: Long): List<DbBalancesEntity> {
        return balanceDao.getBalanceById(idBalance)
    }

    @Transaction
    suspend fun getBalanceByParticipante(idParticipante: Long): List<DbBalancesEntity> {
        return balanceDao.getBalanceByParticipante(idParticipante)
    }


    suspend fun updateBalance(balance: DbBalancesEntity): Boolean {
        val actualizado = balanceDao.updateBalance(balance)
        return actualizado > 0
    }

    @Delete
    suspend fun deleteBalance(balance: DbBalancesEntity) {
        balanceDao.deleteBalance(balance)
    }

    @Transaction
    @Insert
    suspend fun insertBalancesForHoja(
        hoja: DbHojaCalculoEntity,
        balances: List<DbBalancesEntity>
    ) {
        balanceDao.insertBalancesForHoja(hoja, balances)
    }

    /**********/
    /** API **/
    /**********/
    // Obtener todos los balances
    suspend fun getBalancesApi(): List<BalanceDto>? {
        return balancesRepository.getBalances()
    }

    // Obtener un balance por ID
    suspend fun getBalanceByIdApi(id: Long): BalanceDto? {
        return balancesRepository.getBalanceById(id)
    }

    // Obtener balances segun la condicion
    suspend fun getBalanceByApi(column: String, query: String): List<BalanceDto>? {
        return balancesRepository.getBalanceBy(column, query)
    }

    // Crear un nuevo balance
    suspend fun postBalanceApi(balanceCrearDto: BalanceCrearDto): BalanceDto? {
        return balancesRepository.postBalance(balanceCrearDto)
    }

    // Actualizar un balance
    suspend fun putBalanceApi(balanceDto: BalanceDto): BalanceDto? {
        return balancesRepository.putBalance(balanceDto)
    }

    // Eliminar un balance por ID
    suspend fun deleteBalanceApi(id: Long): String? {
        return balancesRepository.deleteBalance(id)
    }

}