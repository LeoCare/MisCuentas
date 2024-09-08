package com.app.miscuentas.data.local.repository

import com.app.miscuentas.data.pattern.dao.DbFotoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbFotoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FotoRepository @Inject constructor(
    private val fotoDao: DbFotoDao
) {

    suspend fun insertFoto(foto: DbFotoEntity): Long {
        return fotoDao.insertFoto(foto)
    }

    fun getAllFotos(): List<DbFotoEntity> = fotoDao.getAllFotos()

    fun getFoto(idFoto: Long): DbFotoEntity = fotoDao.getFoto(idFoto)
}