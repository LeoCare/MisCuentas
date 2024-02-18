package com.app.miscuentas.data.local.repository

import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoDao
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.toDomain
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HojaCalculoRepository @Inject constructor(
    private val hojaCalculoDao: DbHojaCalculoDao
) {
    suspend fun insertAll(hojaCalculo: HojaCalculo) = hojaCalculoDao.insertAll(hojaCalculo.toEntity())

    suspend fun update(hojaCalculo: HojaCalculo) = hojaCalculoDao.update(hojaCalculo.toEntity())

    suspend fun delete(hojaCalculo: HojaCalculo) = hojaCalculoDao.delete(hojaCalculo.toEntity())

    fun getHojaCalculo(id: Int): Flow<HojaCalculo> =
        hojaCalculoDao.getHojaCalculo(id).map { it.toDomain() }

    fun getAllHojasCalculos(): Flow<List<HojaCalculo>> =
        hojaCalculoDao.getAllHojasCalculos().map { list -> list.map { it.toDomain() } }
}