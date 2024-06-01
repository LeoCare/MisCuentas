package com.app.miscuentas.data.local.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.dao.DbHojaCalculoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HojaCalculoRepository @Inject constructor(
    private val hojaCalculoDao: DbHojaCalculoDao
) {

    @Transaction
    @Insert
    suspend fun insertAllHojaCalculo(hojaCalculo: HojaCalculo) {
        hojaCalculoDao.insertAllHojaCalculo(hojaCalculo.toEntity())
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
    suspend fun update(hojaCalculo: HojaCalculo) = hojaCalculoDao.update(hojaCalculo.toEntity())

    @Delete
    suspend fun delete(hojaCalculo: HojaCalculo) = hojaCalculoDao.delete(hojaCalculo.toEntity())


    fun getHojaCalculo(id: Long): Flow<HojaCalculo> =
        hojaCalculoDao.getHojaCalculo(id).map { it.toDomain() }

    fun getAllHojasCalculos(): Flow<List<HojaCalculo>> =
        hojaCalculoDao.getAllHojasCalculos().map { list -> list.map { it.toDomain() } }

    fun getAllHojaConParticipantes(): Flow<List<HojaConParticipantes>> =
        hojaCalculoDao.getAllHojaConParticipantes().map { list -> list.map { it } }

    fun getMaxIdHojasCalculos(): Flow<Long> =
        hojaCalculoDao.getMaxIdHojasCalculos()

    fun getHojaCalculoPrincipal(): Flow<HojaCalculo?> =
        hojaCalculoDao.getHojaCalculoPrincipal().map { it?.toDomain() }

    fun getHojaConParticipantes(id: Long): Flow<HojaConParticipantes?> =
        hojaCalculoDao.getHojaConParticipantes(id)
    /*
    fun getMaxLineaHojasCalculos(id: Int): Flow<Int> =
        hojaCalculoDao.getMaxLineaHojasCalculos(id)

    fun getLineaPartiHojasCalculosLin(id: Int, pagador: Int): Flow<Int> =
        hojaCalculoDao.getLineaPartiHojasCalculosLin(id, pagador)

    fun getMaxLineaDetHojasCalculos(id: Int, linea: Int): Flow<Int?> =
        hojaCalculoDao.getMaxLineaDetHojasCalculos(id, linea)

    */

}
