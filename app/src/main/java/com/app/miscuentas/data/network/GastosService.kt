package com.app.miscuentas.data.network

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.model.Gasto
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.pattern.dao.DbGastoDao
import com.app.miscuentas.data.pattern.repository.GastosRepository
import com.app.miscuentas.data.dto.GastoCrearDto
import com.app.miscuentas.data.dto.GastoDto

class GastosService(
    private val gastoDao: DbGastoDao,
    private val gastosRepository: GastosRepository
) {
    /*****************/
    /** ROOM (local)**/
    /*****************/
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertaGasto(gasto: DbGastosEntity) {
        gastoDao.insertaGasto(gasto)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllGastos(gastos: List<DbGastosEntity>) {
        gastoDao.insertAllGastos(gastos)
    }

    @Update
    suspend fun update(gasto: Gasto, idParticipante: Long) { gastoDao.update(gasto.toEntity(idParticipante))}

    @Update
    suspend fun updateGasto(gasto: DbGastosEntity) { gastoDao.update(gasto)}

    @Delete
    suspend fun delete(gasto: DbGastosEntity) { gastoDao.delete(gasto) }

    /**********/


    /**********/
    /** API **/
    /**********/
    // Obtener todos los gastos
    suspend fun getAllGastos(): List<GastoDto>? {
        return gastosRepository.getAllGastos()
    }

    // Obtener un gasto por ID
    suspend fun getGastoById(id: Long): GastoDto? {
        return gastosRepository.getGastoById(id)
    }

    // Obtener un gasto por ID
    suspend fun getGastoBy(column: String, query: String): List<GastoDto>? {
        return gastosRepository.getGastoBy(column, query)
    }

    // Crear un nuevo gasto
    suspend fun createGasto( gastoCrearDto: GastoCrearDto): GastoDto? {
        return gastosRepository.createGasto(gastoCrearDto)
    }

    // Actualizar un gasto
    suspend fun updateGasto(gastoDto: GastoDto): GastoDto? {
        return gastosRepository.updateGasto(gastoDto)
    }

    // Eliminar un gasto por ID
    suspend fun deleteGasto(id: Long): String? {
        return gastosRepository.deleteGasto(id)
    }
}
