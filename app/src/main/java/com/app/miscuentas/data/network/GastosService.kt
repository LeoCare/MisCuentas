package com.app.miscuentas.data.network

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update
import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.model.Gasto
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.pattern.dao.DbGastoDao
import com.app.miscuentas.data.pattern.repository.GastosRepository
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.GastoCrearDto
import com.app.miscuentas.domain.dto.GastoDto

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
    suspend fun getAllGastos(token: String): List<GastoDto>? {
        return gastosRepository.getAllGastos(token)
    }

    // Obtener un gasto por ID
    suspend fun getGastoById(token: String, id: Long): GastoDto? {
        return gastosRepository.getGastoById(token, id)
    }

    // Crear un nuevo gasto
    suspend fun createGasto(token: String, gastoCrearDto: GastoCrearDto): GastoDto? {
        return gastosRepository.createGasto(token, gastoCrearDto)
    }

    // Actualizar un gasto
    suspend fun updateGasto(token: String, gastoDto: GastoDto): GastoDto? {
        return gastosRepository.updateGasto(token, gastoDto)
    }

    // Eliminar un gasto por ID
    suspend fun deleteGasto(token: String, id: Long): String? {
        return gastosRepository.deleteGasto(token, id)
    }
}
