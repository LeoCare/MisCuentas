package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity

@Dao
interface DbGastoDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllGastos( gastos: List<DbGastosEntity>)

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertaGasto(gasto: DbGastosEntity): Long

    @Update
    suspend fun update(gasto: DbGastosEntity)

    @Delete
    suspend fun delete(gasto: DbGastosEntity)

    @Query("DELETE FROM t_gastos")
    suspend fun clearAllGastos()
}