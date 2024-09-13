package com.app.miscuentas.data.network

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.model.HojaCalculo
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.pattern.dao.DbHojaCalculoDao
import com.app.miscuentas.data.pattern.repository.HojasRepository
import com.app.miscuentas.domain.dto.HojaCrearDto
import com.app.miscuentas.domain.dto.HojaDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HojasService(
    private val hojaCalculoDao: DbHojaCalculoDao,
    private val hojasRepository: HojasRepository
) {
    /*****************/
    /** ROOM (local)**/
    /*****************/
    @Transaction
    @Insert
    suspend fun insertHojaCalculo(hojaCalculo: DbHojaCalculoEntity) {
        hojaCalculoDao.insertHojaCalculo(hojaCalculo)
    }

    @Transaction
    @Insert
    suspend fun insertHojaConParticipantes(
        hoja: DbHojaCalculoEntity,
        participantes: List<DbParticipantesEntity>
    ) {
        hojaCalculoDao.insertHojaConParticipantes(hoja, participantes)
    }


    @Update
    suspend fun updateHoja(hojaCalculo: DbHojaCalculoEntity) = hojaCalculoDao.updateHoja(hojaCalculo)

    @Delete
    suspend fun deleteHojaConParticipantes(hojaCalculo: DbHojaCalculoEntity) = hojaCalculoDao.delete(hojaCalculo)


    fun getHojaCalculo(id: Long): Flow<HojaCalculo> =
        hojaCalculoDao.getHojaCalculo(id).map { it.toDomain() }

    fun getAllHojasCalculos(): Flow<List<HojaCalculo>> =
        hojaCalculoDao.getAllHojasCalculos().map { list -> list.map { it.toDomain() } }

    fun getAllHojaConParticipantes(): Flow<List<HojaConParticipantes>> =
        hojaCalculoDao.getAllHojaConParticipantes().map { list -> list.map { it } }

    fun getAllHojaConParticipantes(idRegistro: Long): Flow<List<HojaConParticipantes>> =
        hojaCalculoDao.getAllHojaConParticipantes(idRegistro).map { list -> list.map { it } }

    fun getMaxIdHojasCalculos(): Flow<Long> =
        hojaCalculoDao.getMaxIdHojasCalculos()

    fun getHojaConParticipantes(id: Long): Flow<HojaConParticipantes?> =
        hojaCalculoDao.getHojaConParticipantes(id)

    fun getHojaConBalances(id: Long): Flow<HojaConBalances?> =
        hojaCalculoDao.getHojaConBalances(id)

    fun getTotalHojasCreadas() : Flow<Int> =
        hojaCalculoDao.getTotalHojasCreadas()
    /**********/


    /**********/
    /** API **/
    /**********/
    // Obtener todas las hojas
    suspend fun getAllHojas(token: String): List<HojaDto>? {
        return hojasRepository.getAllHojas(token)
    }

    // Obtener una hoja por ID
    suspend fun getHojaById(token: String, id: Long): HojaDto? {
        return hojasRepository.getHojaById(token, id)
    }

    // Crear una nueva hoja
    suspend fun createHoja(token: String, hojaCrearDto: HojaCrearDto): HojaDto? {
        return hojasRepository.createHoja(token, hojaCrearDto)
    }

    // Actualizar una hoja
    suspend fun updateHoja(token: String, hojaDto: HojaDto): HojaDto? {
        return hojasRepository.updateHoja(token, hojaDto)
    }

    // Eliminar una hoja por ID
    suspend fun deleteHoja(token: String, id: Long): String? {
        return hojasRepository.deleteHoja(token, id)
    }
}
