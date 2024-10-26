package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.network.ImagenesService
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.di.WithInterceptor
import com.app.miscuentas.domain.dto.ImagenCrearDto
import com.app.miscuentas.domain.dto.ImagenDto

class ImagenesRepository(
    @WithInterceptor
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todas las imágenes
    suspend fun getAllImagenes(token: String): List<ImagenDto>? {
        return try {
            val response = webService.getAllImagenes()
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener imágenes: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Obtener una imagen por ID
    suspend fun getImagenById(token: String, id: Long): ImagenDto? {
        return try {
            val response = webService.getImagenById( id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener imagen: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Crear una nueva imagen
    suspend fun createImagen(token: String, imagenCrearDto: ImagenCrearDto): ImagenDto? {
        return try {
            val response = webService.createImagen( imagenCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear imagen: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Actualizar una imagen
    suspend fun updateImagen(token: String, imagenDto: ImagenDto): ImagenDto? {
        return try {
            val response = webService.updateImagen( imagenDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al actualizar imagen: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Eliminar una imagen por ID
    suspend fun deleteImagen(token: String, id: Long): String? {
        return try {
            val response = webService.deleteImagen( id)
            if (response.isSuccessful) {
                response.body()?.toString()
            } else {
                throw Exception("Error al eliminar imagen: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }
}
