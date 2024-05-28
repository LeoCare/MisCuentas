package com.app.miscuentas.data.local.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoDao
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntityLin
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoEntityLinDet
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.toDomain
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HojaCalculoRepository @Inject constructor(
    private val hojaCalculoDao: DbHojaCalculoDao
) {

    @Transaction
    @Insert
    suspend fun insertAllHojaCalculo(hojaCalculo: HojaCalculo) { hojaCalculoDao.insertAllHojaCalculo(hojaCalculo.toEntity()) }

    @Transaction
    @Insert
    suspend fun insertAllHojaCalculoLin(hojaCalculoEntitylin: DbHojaCalculoEntityLin) { hojaCalculoDao.insertAllHojaCalculoLin(hojaCalculoEntitylin) }

    @Transaction
    @Insert
    suspend fun insertAllHojaCalculoLinDet(hojaCalculoEntitylinDet: DbHojaCalculoEntityLinDet) { hojaCalculoDao.insertAllHojaCalculoLinDet(hojaCalculoEntitylinDet) }

    @Update
    suspend fun update(hojaCalculo: HojaCalculo) = hojaCalculoDao.update(hojaCalculo.toEntity())

    @Delete
    suspend fun delete(hojaCalculo: HojaCalculo) = hojaCalculoDao.delete(hojaCalculo.toEntity())


    fun deleteGasto(idHoja: Int, idParticipante: Int, idGasto: Int) = hojaCalculoDao.deleteGasto(idHoja, idParticipante, idGasto)

    fun getHojaCalculo(id: Int): Flow<HojaCalculo> =
        hojaCalculoDao.getHojaCalculo(id).map { it.toDomain() }

    fun getAllHojasCalculos(): Flow<List<HojaCalculo>> =
        hojaCalculoDao.getAllHojasCalculos().map { list -> list.map { it.toDomain() } }

    fun getMaxIdHojasCalculos(): Flow<Int> =
        hojaCalculoDao.getMaxIdHojasCalculos()

    fun getMaxLineaHojasCalculos(id: Int): Flow<Int> =
        hojaCalculoDao.getMaxLineaHojasCalculos(id)

    fun getLineaPartiHojasCalculosLin(id: Int, pagador: Int): Flow<Int> =
        hojaCalculoDao.getLineaPartiHojasCalculosLin(id, pagador)

    fun getMaxLineaDetHojasCalculos(id: Int, linea: Int): Flow<Int?> =
        hojaCalculoDao.getMaxLineaDetHojasCalculos(id, linea)

    fun getHojaCalculoPrincipal(): Flow<HojaCalculo?> =
        hojaCalculoDao.getHojaCalculoPrincipal().map { it?.toDomain() }

}
