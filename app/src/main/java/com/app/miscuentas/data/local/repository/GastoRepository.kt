package com.app.miscuentas.data.local.repository

import com.app.miscuentas.data.local.dbroom.dbGastos.DbGastoDao
import com.app.miscuentas.data.local.dbroom.dbHojaCalculo.DbHojaCalculoDao
import com.app.miscuentas.domain.model.Gasto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GastoRepository @Inject constructor(
    private val gastoDao: DbGastoDao,
    private val hojaCalculoDao: DbHojaCalculoDao
) {

//    @Transaction
//    suspend fun insertAllGastos(gasto: Gasto) { gastoDao.insertAllGastos(gasto.toEntity())}
//
//    @Transaction
//    suspend fun update(gasto: Gasto) { gastoDao.update(gasto.toEntity())}
//
//    @Transaction
//    suspend fun delete(gasto: Gasto) { gastoDao.delete(gasto.toEntity())}
//
//    fun getGastos(idHoja: Int, idParticipante: Int): Flow<List<Gasto>> =
//        hojaCalculoDao.getGastos(idHoja, idParticipante).map { list -> list.map { it.toDomain() } }

    fun getGastosParticipante(idHoja: Int, idParticipante: Int): Flow<List<Gasto?>> =
        hojaCalculoDao.getGastosParticipante(idHoja, idParticipante).map { list -> list.map { it } }
}