package com.app.miscuentas.data.network

import androidx.room.Update
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.toEntityWithHoja
import com.app.miscuentas.data.pattern.dao.DbParticipantesDao
import com.app.miscuentas.data.pattern.repository.ParticipantesRepository
import com.app.miscuentas.data.dto.ParticipanteCrearDto
import com.app.miscuentas.data.dto.ParticipanteDto
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
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

    @Update
    suspend fun update( participante: DbParticipantesEntity) =
        participantesDao.update(participante)

    suspend fun updateWithHoja(idHojaCalculo: Long, participante: Participante) =
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
    suspend fun getAllParticipantesApi(): List<ParticipanteDto>? {
        return participantesRepository.getAllParticipantes()
    }

    // Obtener un participante por ID
    suspend fun getParticipanteByIdAPI(id: Long): ParticipanteDto? {
        return participantesRepository.getParticipanteById(id)
    }

    // Obtener una hoja segun consulta
    suspend fun getParticipantesByAPI(column: String, query: String):  List<ParticipanteDto>? {
        return participantesRepository.getParticipantesBy(column, query)
    }

    // Crear un nuevo participante
    suspend fun createParticipanteAPI(participanteCrearDto: ParticipanteCrearDto): ParticipanteDto? {
        return participantesRepository.createParticipante(participanteCrearDto)
    }

    // Actualizar un participante
    suspend fun updateParticipanteAPI(participanteDto: ParticipanteDto): ParticipanteDto? {
        return participantesRepository.updateParticipante(participanteDto)
    }

    // Eliminar un participante por ID
    suspend fun deleteParticipanteAPI(id: Long): String? {
        return participantesRepository.deleteParticipante(id)
    }
}
