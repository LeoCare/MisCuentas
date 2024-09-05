package com.app.miscuentas.domain

import com.app.miscuentas.data.network.imagen.ImagenesRepository
import com.app.miscuentas.data.network.imagen.ImagenesService
import com.app.miscuentas.domain.dto.ImagenCrearDto
import com.app.miscuentas.domain.dto.ImagenDto

class GetImagenes(
    private val imagenesRepository: ImagenesRepository
) {

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
