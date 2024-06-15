package com.app.miscuentas.data.local.dbroom.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity

@Dao
interface DbPagoDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPago(pago: DbPagoEntity): Long

    @Transaction
    @Query("SELECT * FROM t_pagos WHERE idBalance = :idDeuda")
    suspend fun getPagosByDeuda(idDeuda: Long): List<DbPagoEntity>

    @Transaction
    @Delete
    suspend fun deletePago(pago: DbPagoEntity)
}