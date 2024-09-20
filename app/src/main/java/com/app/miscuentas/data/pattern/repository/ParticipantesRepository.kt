package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.data.dto.ParticipanteCrearDto
import com.app.miscuentas.data.dto.ParticipanteDto

class ParticipantesRepository(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los participantes
    suspend fun getAllParticipantes(): List<ParticipanteDto>? {
        return try {
            val response = webService.getAllParticipantes()
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener participantes: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Obtener un participante por ID
    suspend fun getParticipanteById(id: Long): ParticipanteDto? {
        return try {
            val response = webService.getParticipanteById( id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener participante: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Obtener participantes segun coincidan con la consulta
    suspend fun getParticipantesBy(column: String, query: String): List<ParticipanteDto>? {
        return try {
            val response = webService.getParticipantesWhenData(column, query)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener participantes: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            throw Exception("Error de red al obtener participantes: ${e.message}\", e")
        }
    }

    // Crear un nuevo participante
    suspend fun createParticipante(participanteCrearDto: ParticipanteCrearDto): ParticipanteDto? {
        return try {
            val response = webService.createParticipante( participanteCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear participante: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            throw Exception("Error de red al crear participante: ${e.message}\", e")
        }
    }

    // Actualizar un participante
    suspend fun updateParticipante(participanteDto: ParticipanteDto): ParticipanteDto? {
        return try {
            val response = webService.updateParticipante( participanteDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al actualizar participante: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Eliminar un participante por ID
    suspend fun deleteParticipante(id: Long): String? {
        return try {
            val response = webService.deleteParticipante( id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al eliminar participante: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }
}
