package com.app.miscuentas.data.local.dbroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity

@Dao
interface DbBalanceDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: DbBalanceEntity): Long

    @Transaction
    @Delete
    suspend fun deleteBalance(balance: DbBalanceEntity)

    @Update
    suspend fun updateBalance(balance: DbBalanceEntity): Int

    @Transaction
    @Query("SELECT * FROM t_balance WHERE idHojaBalance = :idHoja")
    suspend fun getBalanceByHoja(idHoja: Long): List<DbBalanceEntity>

    @Transaction
    @Query("SELECT * FROM t_balance WHERE idParticipanteBalance = :idParticipante")
    suspend fun getBalanceByParticipante(idParticipante: Long): List<DbBalanceEntity>




    @Transaction
    suspend fun insertBalancesForHoja(hoja: DbHojaCalculoEntity, balances: List<DbBalanceEntity>) {
        balances.forEach { balance ->
            val balanceConHojaId = balance.copy(idHojaBalance = hoja.idHoja)
            insertBalance(balanceConHojaId)
        }
    }
}