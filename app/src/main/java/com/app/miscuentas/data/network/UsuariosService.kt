package com.app.miscuentas.data.network

import androidx.room.Transaction
import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityWithUsuario
import com.app.miscuentas.data.pattern.dao.DbUsuarioDao
import com.app.miscuentas.data.pattern.repository.UsuariosRepository
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDeleteDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UsuariosService (
    private val usuarioDao: DbUsuarioDao,
    private val usuariosRepository: UsuariosRepository
) {
    /*****************/
    /** ROOM (local)**/
    /*****************/

    //  Registro de usuario (Room))
    suspend fun cleanInsert(usuario: DbUsuariosEntity): Long {
        return usuarioDao.cleanInsert(usuario)
    }

    // Elimina un usuario y lo inserta nuevamente
    suspend fun cleanUserAndInsert(usuario: DbUsuariosEntity): Long {
        return usuarioDao.cleanUserAndInsert(usuario)
    }

    suspend fun cleanAllUsuariosExcept(idUsuario: Long){
        return usuarioDao.clearAllUsuariosExcept(idUsuario)
    }

    //  Registro de usuario (Room))
    suspend fun insert(usuario: DbUsuariosEntity) {
        usuarioDao.insert(usuario)
    }

    //  Actualizar usuario (Room)
    suspend fun update(usuario: DbUsuariosEntity) = usuarioDao.update(usuario)

    //  Eliminar usuario (Room)
    suspend fun delete(usuario: Usuario) = usuarioDao.delete(usuario.toEntity())

    //  Obtener un registro por ID (Room)
    fun getRegistroWhereId(idUsuario: Long): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereId(idUsuario).map { it.toDomain() }

    //  Obtener usuario por login (Room)
    fun getUsuarioWhereLogin(correo: String): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereLogin(correo).map { it?.toDomain() }

    //  Obtener usuario por correo (Room)
    fun getUsuarioWhereCorreo(correo: String): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereCorreo(correo).map { it?.toDomain() }

    //  Obtener usuario por correo (Room)
    fun getCorreoWhereId(id: Long): String =
        usuarioDao.getCorreoWhereId(id)

    //  Limpia e insertar usuario con participantes (Room)
    suspend fun insertUsuarioConParticipantes(
        usuario: Usuario,
        participante: Participante
    ): Long {
        val dbUsuario = usuario.toEntity()
        val dbParticipante = participante.toEntityWithUsuario(0, usuario.idUsuario) // Inicialmente sin idRegistro
        return usuarioDao.clearInsertUsuarioConParticipante(dbUsuario, dbParticipante)
    }
    /**********/


    /**********/
    /** API **/
    /**********/

    // Registrar usuario (API)
    suspend fun putRegistroApi(usuarioCrearDto: UsuarioCrearDto): UsuarioWithTokenDto? {
        return usuariosRepository.putRegistro(usuarioCrearDto)
    }

    // Iniciar sesión de un usuario (API)
    suspend fun postLoginApi(usuarioLoginDto: UsuarioLoginDto): UsuarioWithTokenDto? {
        return usuariosRepository.postLogin(usuarioLoginDto)
    }

    // Verificar existencia del correo (API)
    suspend fun verifyCorreoApi(correo: String): UsuarioDto? {
        return usuariosRepository.verifyCorreo(correo)
    }

    // Verificar existencia del correo (API)
    suspend fun verifyCodigoApi(correo: String, codigo: String): String? {
        return usuariosRepository.verifyCodigo(correo, codigo)
    }

    // Obtener la lista de todos los usuarios (API)
    suspend fun getUsuariosApi(): List<UsuarioDto>? {
        return usuariosRepository.getUsuarios()
    }

    // Obtener usuarios filtrados por una columna específica (API)
    suspend fun getUsuarioWhenDataApi(column: String, query: String): List<UsuarioDto>? {
        return usuariosRepository.getUsuarioWhenData(column, query)
    }

    // Obtener un usuario por ID (API)
    suspend fun getUsuarioByIdApi(id: Long): UsuarioDto? {
        return usuariosRepository.getUsuarioById(id)
    }

    // Actualizar un usuario (API)
    suspend fun putUsuarioApi(usuarioDto: UsuarioDto): String? {
        return usuariosRepository.putUsuario(usuarioDto)
    }

    // Actualizar pass de un usuario (API)
    suspend fun putUsuarioNewPassApi(usuarioDto: UsuarioDto): UsuarioDto? {
        return usuariosRepository.putUsuarioNewPass(usuarioDto)
    }

    // Eliminar un usuario (API)
    suspend fun deleteUsuarioApi(usuarioDeleteDto: UsuarioDeleteDto): String? {
        return usuariosRepository.deleteUsuario(usuarioDeleteDto)
    }

    /**********/

}

