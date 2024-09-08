package com.app.miscuentas.data.pattern

import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.domain.dto.ParticipanteCrearDto
import com.app.miscuentas.domain.dto.ParticipanteDto

class ParticipantesRepository(
    private val participanteService: ParticipantesService
) {

    // Obtener todos los participantes
    suspend fun getAllParticipantes(token: String): List<ParticipanteDto>? {
        return participanteService.getAllParticipantes(token)
    }

    // Obtener un participante por ID
    suspend fun getParticipanteById(token: String, id: Long): ParticipanteDto? {
        return participanteService.getParticipanteById(token, id)
    }

    // Crear un nuevo participante
    suspend fun createParticipante(token: String, participanteCrearDto: ParticipanteCrearDto): ParticipanteDto? {
        return participanteService.createParticipante(token, participanteCrearDto)
    }

    // Actualizar un participante
    suspend fun updateParticipante(token: String, participanteDto: ParticipanteDto): ParticipanteDto? {
        return participanteService.updateParticipante(token, participanteDto)
    }

    // Eliminar un participante por ID
    suspend fun deleteParticipante(token: String, id: Long): String? {
        return participanteService.deleteParticipante(token, id)
    }
}
