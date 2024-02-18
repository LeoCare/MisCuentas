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
    suspend fun insertAll(participante: Participante) = participantesDao.insertAll(participante.toEntity())

    suspend fun update(participante: Participante) = participantesDao.update(participante.toEntity())

    suspend fun delete(participante: Participante) = participantesDao.delete(participante.toEntity())

    fun getParticipante(id: Int): Flow<Participante> =
        participantesDao.getParticipante(id).map { it.toDomain() }

    fun getAllParticipantes(): Flow<List<Participante>> =
        participantesDao.getAllParticipantes().map { list -> list.map { it.toDomain() } }

}