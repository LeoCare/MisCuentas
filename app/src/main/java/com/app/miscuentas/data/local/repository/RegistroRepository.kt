package com.app.miscuentas.data.local.repository

import android.provider.ContactsContract.Contacts.AggregationSuggestions
import com.app.miscuentas.data.local.dbroom.dbRegistros.DbRegistroDao
import com.app.miscuentas.data.local.dbroom.dbRegistros.toDomain
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.domain.model.Registro
import com.app.miscuentas.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RegistroRepository @Inject constructor(
    private val registroDao: DbRegistroDao
) {

    suspend fun insertAll(registro: Registro) = registroDao.insertAll(registro.toEntity())

    suspend fun update(registro: Registro) = registroDao.update(registro.toEntity())

    suspend fun delete(registro: Registro) = registroDao.delete(registro.toEntity())

    fun getRegistro(nombre: String, contrasenna: String): Flow<Registro?> =
        registroDao.getRegistro(nombre, contrasenna).map { it?.toDomain() }

    fun getRegistroExist(correo: String): Flow<Registro?> =
        registroDao.getRegistroExist(correo).map { it?.toDomain() }
}