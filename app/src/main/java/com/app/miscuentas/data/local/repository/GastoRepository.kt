package com.app.miscuentas.data.local.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.dao.DbGastoDao
import com.app.miscuentas.data.local.dbroom.dao.DbHojaCalculoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GastoRepository @Inject constructor(
    private val gastoDao: DbGastoDao,
    private val hojaCalculoDao: DbHojaCalculoDao
) {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertaGasto(gasto: DbGastosEntity) {
        gastoDao.insertaGasto(gasto)
    }

    @Update
    suspend fun update(gasto: Gasto, idParticipante: Long) { gastoDao.update(gasto.toEntity(idParticipante))}

    @Delete
    suspend fun delete(gasto: DbGastosEntity?) { gastoDao.delete(gasto) }

}