package com.app.miscuentas.data.network

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.pattern.dao.DbPagoDao
import com.app.miscuentas.data.pattern.repository.PagosRepository
import com.app.miscuentas.data.dto.PagoCrearDto
import com.app.miscuentas.data.dto.PagoDto

class PagosService(
    private val pagoDao: DbPagoDao,
    private val pagosRepository: PagosRepository
) {
    /*****************/
    /** ROOM (local)**/
    /*****************/
    suspend fun insertPago(pago: DbPagoEntity): Long {
        return pagoDao.insertPago(pago)
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAllPagos(pagos: List<DbPagoEntity>) {
        pagoDao.insertAllPagos(pagos)
    }

    suspend fun getPagosByDeuda(idDeuda: Long): List<DbPagoEntity> {
        return pagoDao.getPagosByDeuda(idDeuda)
    }

    suspend fun getPagosById(idPago: Long): DbPagoEntity {
        return pagoDao.getPagosById(idPago)
    }

    suspend fun updatePago(pago: DbPagoEntity) {
        pagoDao.updatePago(pago)
    }

    suspend fun deletePago(pago: DbPagoEntity) {
        pagoDao.deletePago(pago)
    }

    /**********/


    /**********/
    /** API **/
    /**********/
    // Obtener todos los pagos
    suspend fun getAllPagos(): List<PagoDto>? {
        return pagosRepository.getAllPagos()
    }

    // Obtener un pago por ID
    suspend fun getPagoById(id: Long): PagoDto? {
        return pagosRepository.getPagoById(id)
    }

    // Obtener todos los pagos segun condicion
    suspend fun getPagosBy(column: String, query: String): List<PagoDto>? {
        return pagosRepository.getPagosBy(column, query)
    }

    // Crear un nuevo pago
    suspend fun createPago(pagoCrearDto: PagoCrearDto): PagoDto? {
        return pagosRepository.createPago(pagoCrearDto)
    }

    // Actualizar un pago
    suspend fun updatePago(pagoDto: PagoDto): PagoDto? {
        return pagosRepository.updatePago(pagoDto)
    }

    // Eliminar un pago por ID
    suspend fun deletePago(id: Long): String? {
        return pagosRepository.deletePago(id)
    }
}
