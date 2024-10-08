package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator

import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.data.dto.HojaCrearDto
import com.app.miscuentas.data.dto.HojaDto
import com.app.miscuentas.di.WithInterceptor

class HojasRepository(
    @WithInterceptor
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {
    // Obtener todas las hojas
    suspend fun getAllHojas(): List<HojaDto>? {
        return try {
            val response = webService.getAllHojas()
            if (response.isSuccessful) {
                response.body()
            }
            else {
                throw Exception("Error al obtener hojas: ${response.code()} - ${response.message()}")
            }
//        } catch (e: TokenExpiredException) {
//            // Redirige al usuario a la pantalla de inicio de sesión
//            // O muestra un mensaje de error, etc.
//
//            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Obtener una hoja por ID
    suspend fun getHojaById(id: Long): HojaDto? {
        return try {
            val response = webService.getHojaById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener hoja: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Error de red al obtener hoja: ${e.message}", e)
        }
    }

    // Obtener una lista de hojas segun consulta
    suspend fun getHojaBy(column: String, query: String): List<HojaDto>? {
        return try {
            val response = webService.getHojasWhenData(column, query)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener lista de hojas: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Crear una nueva hoja
    suspend fun createHoja(hojaCrearDto: HojaCrearDto): HojaDto? {
        return try {
            val response = webService.createHoja(hojaCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear hoja: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar una hoja
    suspend fun updateHoja(hojaDto: HojaDto): HojaDto? {
        return try {
            val response = webService.updateHoja(hojaDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al actualizar hoja: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Error de red al actualizar hoja: ${e.message}", e)
        }
    }

    // Eliminar una hoja por ID
    suspend fun deleteHoja(id: Long): String? {
        return try {
            val response = webService.deleteHoja(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al eliminar hoja: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            throw Exception("Error de red al eliminar hoja: ${e.message}", e)
        }
    }

}
