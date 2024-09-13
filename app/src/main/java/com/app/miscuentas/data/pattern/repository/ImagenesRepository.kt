package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.network.ImagenesService
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.ImagenCrearDto
import com.app.miscuentas.domain.dto.ImagenDto

class ImagenesRepository(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todas las imágenes
    suspend fun getAllImagenes(token: String): List<ImagenDto>? {
        val response = webService.getAllImagenes(token)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener imágenes: ${response.code()} - ${response.message()}")
        }
    }

    // Obtener una imagen por ID
    suspend fun getImagenById(token: String, id: Long): ImagenDto? {
        val response = webService.getImagenById(token, id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener imagen: ${response.code()} - ${response.message()}")
        }
    }

    // Crear una nueva imagen
    suspend fun createImagen(token: String, imagenCrearDto: ImagenCrearDto): ImagenDto? {
        val response = webService.createImagen(token, imagenCrearDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al crear imagen: ${response.code()} - ${response.message()}")
        }
    }

    // Actualizar una imagen
    suspend fun updateImagen(token: String, imagenDto: ImagenDto): ImagenDto? {
        val response = webService.updateImagen(token, imagenDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al actualizar imagen: ${response.code()} - ${response.message()}")
        }
    }

    // Eliminar una imagen por ID
    suspend fun deleteImagen(token: String, id: Long): String? {
        val response = webService.deleteImagen(token, id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al eliminar imagen: ${response.code()} - ${response.message()}")
        }
    }
}
