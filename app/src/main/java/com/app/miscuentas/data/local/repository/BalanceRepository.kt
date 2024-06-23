package com.app.miscuentas.data.local.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.dao.DbBalanceDao
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import javax.inject.Inject

class BalanceRepository @Inject constructor(
    private val balanceDao: DbBalanceDao
) {
    @Transaction
    @Insert
    suspend fun insertBalance(balance: DbBalanceEntity): Long {
        return balanceDao.insertBalance(balance)
    }

    @Transaction
    suspend fun getBalanceByHoja(idHoja: Long): List<DbBalanceEntity> {
        return balanceDao.getBalanceByHoja(idHoja)
    }

    @Transaction
    suspend fun getBalanceByParticipante(idParticipante: Long): List<DbBalanceEntity> {
        return balanceDao.getBalanceByParticipante(idParticipante)
    }


    suspend fun updateBalance(balance: DbBalanceEntity): Boolean {
        val actualizado = balanceDao.updateBalance(balance)
        return actualizado > 0
    }

    @Delete
    suspend fun deleteBalance(balance: DbBalanceEntity) {
        balanceDao.deleteBalance(balance)
    }

    @Transaction
    @Insert
    suspend fun insertBalancesForHoja(
        hoja: DbHojaCalculoEntity,
        balances: List<DbBalanceEntity>
    ) {
        balanceDao.insertBalancesForHoja(hoja, balances)
    }
}