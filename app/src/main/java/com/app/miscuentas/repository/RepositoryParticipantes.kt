package com.app.miscuentas.repository

import com.app.miscuentas.db.dbParticipantes.DbParticipantesDao
import com.app.miscuentas.db.dbParticipantes.toDomain
import com.app.miscuentas.model.Participante
import com.app.miscuentas.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RepositoryParticipantes @Inject constructor(
    private val participantesDao: DbParticipantesDao
) {
    suspend fun insertAll(participante: Participante) = participantesDao.insertAll(participante.toEntity())

    suspend fun update(participante: Participante) = participantesDao.update(participante.toEntity())

    suspend fun delete(participante: Participante) = participantesDao.delete(participante.toEntity())

    fun getParticipante(id: Int): Flow<Participante> = participantesDao.getParticipante(id)
        .map { it.toDomain()}

    fun getAllParticipantes(): Flow<List<Participante>> = participantesDao.getAllParticipantes()
        .map { list -> list.map { it.toDomain() } }

}