package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbBalancesEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity

@Dao
interface DbBalanceDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: DbBalancesEntity): Long

    @Transaction
    @Delete
    suspend fun deleteBalance(balance: DbBalancesEntity)

    @Query("DELETE FROM t_balance")
    suspend fun clearAllBalances()

    @Update
    suspend fun updateBalance(balance: DbBalancesEntity): Int

    @Transaction
    @Query("SELECT * FROM t_balance WHERE idHojaBalance = :idHoja")
    suspend fun getBalanceByHoja(idHoja: Long): List<DbBalancesEntity>

    @Transaction
    @Query("SELECT * FROM t_balance WHERE idBalance = :idBalance")
    suspend fun getBalanceById(idBalance: Long): List<DbBalancesEntity>

    @Transaction
    @Query("SELECT * FROM t_balance WHERE idParticipanteBalance = :idParticipante")
    suspend fun getBalanceByParticipante(idParticipante: Long): List<DbBalancesEntity>

    @Transaction
    suspend fun insertBalancesForHoja(hoja: DbHojaCalculoEntity, balances: List<DbBalancesEntity>) {
        balances.forEach { balance ->
            val balanceConHojaId = balance.copy(idHojaBalance = hoja.idHoja)
            insertBalance(balanceConHojaId)
        }
    }
}