package com.app.miscuentas.data.network.imagen

import com.app.miscuentas.domain.dto.ImagenCrearDto
import com.app.miscuentas.domain.dto.ImagenDto

class ImagenesRepository(
    private val imagenesService: ImagenesService
) {

    // Obtener todas las imágenes
    suspend fun getAllImagenes(token: String): List<ImagenDto>? {
        return imagenesService.getAllImagenes(token)
    }

    // Obtener una imagen por ID
    suspend fun getImagenById(token: String, id: Long): ImagenDto? {
        return imagenesService.getImagenById(token, id)
    }

    // Crear una nueva imagen
    suspend fun createImagen(token: String, imagenCrearDto: ImagenCrearDto): ImagenDto? {
        return imagenesService.createImagen(token, imagenCrearDto)
    }

    // Actualizar una imagen
    suspend fun updateImagen(token: String, imagenDto: ImagenDto): ImagenDto? {
        return imagenesService.updateImagen(token, imagenDto)
    }

    // Eliminar una imagen por ID
    suspend fun deleteImagen(token: String, id: Long): String? {
        return imagenesService.deleteImagen(token, id)
    }
}
