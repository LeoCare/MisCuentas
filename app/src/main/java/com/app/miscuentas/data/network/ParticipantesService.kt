package com.app.miscuentas.data.network

import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.toEntityWithHoja
import com.app.miscuentas.data.pattern.dao.DbParticipantesDao
import com.app.miscuentas.data.pattern.repository.ParticipantesRepository
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.ParticipanteCrearDto
import com.app.miscuentas.domain.dto.ParticipanteDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ParticipantesService(
    private val participantesDao: DbParticipantesDao,
    private val participantesRepository: ParticipantesRepository
) {
    /*****************/
    /** ROOM (local)**/
    /*****************/
    suspend fun cleanInsert(idHojaCalculo: Long, participante: Participante) =
        participantesDao.cleanInsert( participante.toEntityWithHoja(idHojaCalculo))

    suspend fun insertAll(idHojaCalculo: Long, participante: Participante) =
        participantesDao.insertAll( participante.toEntityWithHoja(idHojaCalculo))

    suspend fun update(idHojaCalculo: Long, participante: Participante) =
        participantesDao.update(participante.toEntityWithHoja(idHojaCalculo))

    suspend fun delete(idHojaCalculo: Long, participante: Participante) =
        participantesDao.delete(participante.toEntityWithHoja(idHojaCalculo))

    fun getParticipanteConGastos(idRegistro: Long): Flow<ParticipanteConGastos> =
        participantesDao.getParticipanteConGastos(idRegistro)

    fun getParticipante(id: Int): Flow<Participante> =
        participantesDao.getParticipante(id).map { it.toDomain() }

    fun getAllParticipantes(): Flow<List<Participante>> =
        participantesDao.getAllParticipantes().map { list -> list.map { it.toDomain() } }

    fun getIdParticipante(nombre: String): Int =
        participantesDao.getIdParticipante(nombre)

    fun getMaxIdParticipantes(): Int =
        participantesDao.getMaxIdParticipantes()

    /*
     fun getListParticipantesToIdHoja(idHoja: Int): Flow<List<Participante>> =
        participantesDao.getListParticipantesToIdHoja(idHoja).map { list -> list.map { it.toDomain() } }
     */

    /**********/


    /**********/
    /** API **/
    /**********/
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
