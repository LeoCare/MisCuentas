package com.app.miscuentas.data.pattern.repository

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.ParticipanteCrearDto
import com.app.miscuentas.domain.dto.ParticipanteDto

class ParticipantesRepository(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator
) {

    // Obtener todos los participantes
    suspend fun getAllParticipantes(token: String): List<ParticipanteDto>? {
        return try {
            val response = webService.getAllParticipantes(token)
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
    suspend fun getParticipanteById(token: String, id: Long): ParticipanteDto? {
        return try {
            val response = webService.getParticipanteById(token, id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener participante: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Crear un nuevo participante
    suspend fun createParticipante(token: String, participanteCrearDto: ParticipanteCrearDto): ParticipanteDto? {
        return try {
            val response = webService.createParticipante(token, participanteCrearDto)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al crear participante: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    // Actualizar un participante
    suspend fun updateParticipante(token: String, participanteDto: ParticipanteDto): ParticipanteDto? {
        return try {
            val response = webService.updateParticipante(token, participanteDto)
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
    suspend fun deleteParticipante(token: String, id: Long): String? {
        return try {
            val response = webService.deleteParticipante(token, id)
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
