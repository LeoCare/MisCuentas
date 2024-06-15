package com.app.miscuentas.data.local.repository

import com.app.miscuentas.data.local.dbroom.dao.DbPagoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import javax.inject.Inject

class PagoRepository @Inject constructor(
    private val pagoDao: DbPagoDao
) {
    suspend fun insertPago(pago: DbPagoEntity): Long {
        return pagoDao.insertPago(pago)
    }

    suspend fun getPagosByDeuda(idDeuda: Long): List<DbPagoEntity> {
        return pagoDao.getPagosByDeuda(idDeuda)
    }

    suspend fun deletePago(pago: DbPagoEntity) {
        pagoDao.deletePago(pago)
    }
}