package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity

@Dao
interface DbGastoDao {

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllGastos( gasto: DbGastosEntity): Long

    @Transaction
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertaGasto(gasto: DbGastosEntity): Long

    @Update
    suspend fun update(gasto: DbGastosEntity)

    @Delete
    suspend fun delete(gasto: DbGastosEntity)

}