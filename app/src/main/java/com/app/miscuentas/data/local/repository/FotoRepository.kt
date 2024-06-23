package com.app.miscuentas.data.local.repository

import com.app.miscuentas.data.local.dbroom.dao.DbFotoDao
import com.app.miscuentas.data.local.dbroom.entitys.DbFotoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class FotoRepository @Inject constructor(
    private val fotoDao: DbFotoDao
) {

    fun getAllPhotos(): Flow<List<DbFotoEntity>> = fotoDao.getAllPhotos()

    suspend fun insertFoto(photo: DbFotoEntity) = fotoDao.insertFoto(photo)

}