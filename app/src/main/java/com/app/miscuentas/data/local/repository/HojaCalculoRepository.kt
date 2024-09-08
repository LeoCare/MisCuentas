package com.app.miscuentas.data.local.repository

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Transaction
import androidx.room.Update
import com.app.miscuentas.data.pattern.dao.DbHojaCalculoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbBalanceEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConBalances
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.model.HojaCalculo
import com.app.miscuentas.data.model.toEntity
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

}
