package com.app.miscuentas.data.pattern.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
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
    @Query("SELECT * FROM t_pagos WHERE idPago = :idPago")
    suspend fun getPagosById(idPago: Long): DbPagoEntity

    @Update
    suspend fun updatePago(pago: DbPagoEntity)

    @Transaction
    @Delete
    suspend fun deletePago(pago: DbPagoEntity)

    @Query("DELETE FROM t_pagos")
    suspend fun clearAllPagos()
}