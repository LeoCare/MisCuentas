package com.app.miscuentas.data.local.repository

import com.app.miscuentas.data.local.dbroom.dao.DbParticipantesDao
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ParticipanteRepository @Inject constructor(
    private val participantesDao: DbParticipantesDao
) {
    suspend fun insertAll(idHojaCalculo: Long, participante: Participante) =
        participantesDao.insertAll( participante.toEntity(idHojaCalculo))

    suspend fun update(idHojaCalculo: Long, participante: Participante) =
        participantesDao.update(participante.toEntity(idHojaCalculo))

    suspend fun delete(idHojaCalculo: Long, participante: Participante) =
        participantesDao.delete(participante.toEntity(idHojaCalculo))

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

}