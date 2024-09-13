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
        val response = webService.getAllParticipantes(token)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener participantes: ${response.code()} - ${response.message()}")
        }
    }

    // Obtener un participante por ID
    suspend fun getParticipanteById(token: String, id: Long): ParticipanteDto? {
        val response = webService.getParticipanteById(token, id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener participante: ${response.code()} - ${response.message()}")
        }
    }

    // Crear un nuevo participante
    suspend fun createParticipante(token: String, participanteCrearDto: ParticipanteCrearDto): ParticipanteDto? {
        val response = webService.createParticipante(token, participanteCrearDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al crear participante: ${response.code()} - ${response.message()}")
        }
    }

    // Actualizar un participante
    suspend fun updateParticipante(token: String, participanteDto: ParticipanteDto): ParticipanteDto? {
        val response = webService.updateParticipante(token, participanteDto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al actualizar participante: ${response.code()} - ${response.message()}")
        }
    }

    // Eliminar un participante por ID
    suspend fun deleteParticipante(token: String, id: Long): String? {
        val response = webService.deleteParticipante(token, id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al eliminar participante: ${response.code()} - ${response.message()}")
        }
    }
}
