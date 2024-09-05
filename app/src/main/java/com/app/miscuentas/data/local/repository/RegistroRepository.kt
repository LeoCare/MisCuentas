package com.app.miscuentas.data.local.repository

import androidx.room.Insert
import androidx.room.Transaction
import com.app.miscuentas.data.local.dbroom.dao.DbRegistroDao
import com.app.miscuentas.data.local.dbroom.entitys.DbRegistrosEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.Registro
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityWithRegistro
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RegistroRepository @Inject constructor(
    private val registroDao: DbRegistroDao
) {

    suspend fun insertAll(registro: Registro) = registroDao.insertAll(registro.toEntity())

    suspend fun insert(registro: Registro) = registroDao.insert(registro.toEntity())

    suspend fun update(registro: Registro) = registroDao.update(registro.toEntity())

    suspend fun delete(registro: Registro) = registroDao.delete(registro.toEntity())

    fun getRegistroWhereLogin(nombre: String, contrasenna: String): Flow<Registro?> =
        registroDao.getRegistroWhereLogin(nombre, contrasenna).map { it?.toDomain() }

    fun getRegistroWhereCorreo(correo: String): Flow<Registro?> =
        registroDao.getRegistroWhereCorreo(correo).map { it?.toDomain() }

    fun getRegistroWhereId(idRegistro: Long): Flow<Registro?> =
        registroDao.getRegistroWhereId(idRegistro).map { it?.toDomain() }

    @Transaction
    suspend fun insertRegistroConParticipante(
        registro: Registro,
        participante: Participante
    ): Long {
        val dbRegistro = registro.toEntity()
        val dbParticipante = participante.toEntityWithRegistro(0) // Inicialmente sin idRegistro
        return registroDao.insertRegistroConParticipante(dbRegistro, dbParticipante)

    }



}