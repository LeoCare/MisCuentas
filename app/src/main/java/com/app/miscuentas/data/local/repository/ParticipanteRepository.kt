package com.app.miscuentas.data.local.repository

import com.app.miscuentas.data.local.dbroom.dbParticipantes.DbParticipantesDao
import com.app.miscuentas.data.local.dbroom.dbParticipantes.toDomain
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ParticipanteRepository @Inject constructor(
    private val participantesDao: DbParticipantesDao
) {
    suspend fun insertAll(id: Int, participante: Participante) =
        participantesDao.insertAll( participante.toEntity(id))

    suspend fun update(id: Int, participante: Participante) =
        participantesDao.update(participante.toEntity(id))

    suspend fun delete(id: Int, participante: Participante) =
        participantesDao.delete(participante.toEntity(id))

    fun getParticipante(id: Int): Flow<Participante> =
        participantesDao.getParticipante(id).map { it.toDomain() }

    fun getAllParticipantes(): Flow<List<Participante>> =
        participantesDao.getAllParticipantes().map { list -> list.map { it.toDomain() } }

    fun getIdParticipante(nombre: String): Int =
        participantesDao.getIdParticipante(nombre)

    fun getListParticipantesToIdHoja(idHoja: Int): Flow<List<Participante>> =
        participantesDao.getListParticipantesToIdHoja(idHoja).map { list -> list.map { it.toDomain() } }

    fun getMaxIdParticipantes(): Int =
        participantesDao.getMaxIdParticipantes()

}