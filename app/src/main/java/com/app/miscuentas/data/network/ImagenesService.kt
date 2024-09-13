package com.app.miscuentas.data.network

import com.app.miscuentas.data.local.dbroom.entitys.DbFotosEntity
import com.app.miscuentas.data.pattern.dao.DbImagenDao
import com.app.miscuentas.data.pattern.repository.ImagenesRepository
import com.app.miscuentas.domain.dto.ImagenCrearDto
import com.app.miscuentas.domain.dto.ImagenDto

class ImagenesService(
    private val fotoDao: DbImagenDao,
    private val imagenesRepository: ImagenesRepository
) {

    /*****************/
    /** ROOM (local)**/
    /*****************/
    suspend fun insertFoto(foto: DbFotosEntity): Long {
        return fotoDao.insertFoto(foto)
    }

    fun getAllFotos(): List<DbFotosEntity> = fotoDao.getAllFotos()

    fun getFoto(idFoto: Long): DbFotosEntity = fotoDao.getFoto(idFoto)

    /**********/


    /**********/
    /** API **/
    /**********/
    // Obtener todas las imágenes
    suspend fun getAllImagenes(token: String): List<ImagenDto>? {
        return imagenesRepository.getAllImagenes(token)
    }

    // Obtener una imagen por ID
    suspend fun getImagenById(token: String, id: Long): ImagenDto? {
        return imagenesRepository.getImagenById(token, id)
    }

    // Crear una nueva imagen
    suspend fun createImagen(token: String, imagenCrearDto: ImagenCrearDto): ImagenDto? {
        return imagenesRepository.createImagen(token, imagenCrearDto)
    }

    // Actualizar una imagen
    suspend fun updateImagen(token: String, imagenDto: ImagenDto): ImagenDto? {
        return imagenesRepository.updateImagen(token, imagenDto)
    }

    // Eliminar una imagen por ID
    suspend fun deleteImagen(token: String, id: Long): String? {
        return imagenesRepository.deleteImagen(token, id)
    }
}
