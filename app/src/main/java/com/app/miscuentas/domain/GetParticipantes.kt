package com.app.miscuentas.domain

import com.app.miscuentas.data.network.participante.ParticipantesRepository
import com.app.miscuentas.domain.dto.ParticipanteCrearDto
import com.app.miscuentas.domain.dto.ParticipanteDto

class GetParticipantes(
    private val participantesRepository: ParticipantesRepository
) {

    // Obtener todos los participantes
    suspend fun getAllParticipantes(token: String): List<ParticipanteDto>? {
        return participantesRepository.getAllParticipantes(token)
    }

    // Obtener un participante por ID
    suspend fun getParticipanteById(token: String, id: Long): ParticipanteDto? {
        return participantesRepository.getParticipanteById(token, id)
    }

    // Crear un nuevo participante
    suspend fun createParticipante(token: String, participanteCrearDto: ParticipanteCrearDto): ParticipanteDto? {
        return participantesRepository.createParticipante(token, participanteCrearDto)
    }

    // Actualizar un participante
    suspend fun updateParticipante(token: String, participanteDto: ParticipanteDto): ParticipanteDto? {
        return participantesRepository.updateParticipante(token, participanteDto)
    }

    // Eliminar un participante por ID
    suspend fun deleteParticipante(token: String, id: Long): String? {
        return participantesRepository.deleteParticipante(token, id)
    }
}