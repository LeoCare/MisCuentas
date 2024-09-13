package com.app.miscuentas.data.network

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.pattern.dao.DbPagoDao
import com.app.miscuentas.data.pattern.repository.PagosRepository
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.PagoCrearDto
import com.app.miscuentas.domain.dto.PagoDto

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
    suspend fun getAllPagos(token: String): List<PagoDto>? {
        return pagosRepository.getAllPagos(token)
    }

    // Obtener un pago por ID
    suspend fun getPagoById(token: String, id: Long): PagoDto? {
        return pagosRepository.getPagoById(token, id)
    }

    // Crear un nuevo pago
    suspend fun createPago(token: String, pagoCrearDto: PagoCrearDto): PagoDto? {
        return pagosRepository.createPago(token, pagoCrearDto)
    }

    // Actualizar un pago
    suspend fun updatePago(token: String, pagoDto: PagoDto): PagoDto? {
        return pagosRepository.updatePago(token, pagoDto)
    }

    // Eliminar un pago por ID
    suspend fun deletePago(token: String, id: Long): String? {
        return pagosRepository.deletePago(token, id)
    }
}
