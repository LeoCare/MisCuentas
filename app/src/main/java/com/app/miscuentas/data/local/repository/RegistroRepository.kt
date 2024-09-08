package com.app.miscuentas.data.local.repository
/*
import androidx.room.Transaction
import com.app.miscuentas.data.local.dbroom.dao.DbUsuarioDao
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.Registro
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityWithUsuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class RegistroRepository @Inject constructor(
    private val usuarioDao: DbUsuarioDao
) {

    suspend fun insertAll(registro: Registro) = usuarioDao.insertAll(registro.toEntity())

    suspend fun insert(registro: Registro) = usuarioDao.insert(registro.toEntity())

    suspend fun update(registro: Registro) = usuarioDao.update(registro.toEntity())

    suspend fun delete(registro: Registro) = usuarioDao.delete(registro.toEntity())

    fun getUsuarioWhereLogin(nombre: String, contrasenna: String): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereLogin(nombre, contrasenna).map { it?.toDomain() }

    fun getUsuarioWhereCorreo(correo: String): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereCorreo(correo).map { it?.toDomain() }

    fun getUsuarioWhereId(idUsuario: Long): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereId(idUsuario).map { it?.toDomain() }

    @Transaction
    suspend fun insertRegistroConParticipante(
        registro: Registro,
        participante: Participante
    ): Long {
        val dbRegistro = registro.toEntity()
        val dbParticipante = participante.toEntityWithUsuario(0) // Inicialmente sin idRegistro
        return usuarioDao.insertUsuarioConParticipante(dbRegistro, dbParticipante)
    }
}
*/