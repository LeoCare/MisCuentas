package com.app.miscuentas.data.network

import android.util.Log
import androidx.room.Transaction
import at.favre.lib.crypto.bcrypt.BCrypt
import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.data.model.toDto
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityWithUsuario
import com.app.miscuentas.data.pattern.repository.NetworkBoundResource
import com.app.miscuentas.data.pattern.repository.Resource
import com.app.miscuentas.data.pattern.repository.UsuariosRepository
import com.app.miscuentas.data.pattern.dao.DbUsuarioDao
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDeleteDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UsuariosService (
    private val usuarioDao: DbUsuarioDao,
    private val usuariosRepository: UsuariosRepository
) {
    /*****************/
    /** ROOM (local)**/
    /*****************/

    //  Registro de usuario (Room))
    suspend fun cleanInsert(usuario: DbUsuariosEntity) {
        usuarioDao.cleanInsert(usuario)
    }

    //  Registro de usuario (Room))
    suspend fun insert(usuario: DbUsuariosEntity) {
        usuarioDao.insert(usuario)
    }

    //  Actualizar usuario (Room)
    suspend fun update(usuario: Usuario) = usuarioDao.update(usuario.toEntity())

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

    //  Limpia e insertar usuario con participantes (Room)
    suspend fun insertUsuarioConParticipantes(
        usuario: Usuario,
        participante: Participante
    ): Long {
        val dbUsuario = usuario.toEntity()
        val dbParticipante = participante.toEntityWithUsuario(0) // Inicialmente sin idRegistro
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

    // Obtener la lista de todos los usuarios (API)
    suspend fun getUsuariosApi(token: String): List<UsuarioDto>? {
        return usuariosRepository.getUsuarios(token)
    }

    // Obtener usuarios filtrados por una columna específica (API)
    suspend fun getWhenDataApi(token: String, column: String, query: String): List<UsuarioDto>? {
        return usuariosRepository.getWhenData(token, column, query)
    }

    // Obtener un usuario por ID (API)
    suspend fun getUsuarioByIdApi(token: String, id: Long): UsuarioDto? {
        return usuariosRepository.getUsuarioById(token, id)
    }

    // Actualizar un usuario (API)
    suspend fun putUsuarioApi(token: String, usuarioDto: UsuarioDto): UsuarioDto? {
        return usuariosRepository.putUsuario(token, usuarioDto)
    }

    // Eliminar un usuario (API)
    suspend fun deleteUsuarioApi(token: String, usuarioDeleteDto: UsuarioDeleteDto): String? {
        return usuariosRepository.deleteUsuario(token, usuarioDeleteDto)
    }

    /**********/

    /**************************/
    /** NetworkBoundResource **/
    /**************************/
    fun getRegistroByCorreo(correo: String): Flow<Resource<out UsuarioDto?>> {
        return object : NetworkBoundResource<UsuarioDto?, UsuarioDto>() {

            // Cargar desde la base de datos (Room)
            override fun loadFromDb(): Flow<UsuarioDto?> {
                return usuarioDao.getUsuarioWhereCorreo(correo)
                    .map { dbUsuarioEntity ->
                        dbUsuarioEntity?.toDto() // Retornar null si no existe
                    }
            }

            // Llamar a la API para verificar si el correo existe
            override suspend fun fetchFromNetwork(): UsuarioDto? {
                return usuariosRepository.verifyCorreo(correo)
            }

            // Guardar el resultado de la red en la base de datos
            override suspend fun saveNetworkResult(item: UsuarioDto) {
                usuarioDao.cleanInsert(item.toEntity())
                return
            }

            // Decidir si debemos realizar la llamada a la API (fetch)
            override fun shouldFetch(data: UsuarioDto?): Boolean {
                // Realizamos fetch si los datos locales son nulos
                return data == null
            }

        }.asFlow()
    }

    fun getUsuarioByLogin(correo: String, contrasenna: String): Flow<Resource<out UsuarioDto?>> {
        return object : NetworkBoundResource<UsuarioDto?, UsuarioWithTokenDto>() {

            // Cargar desde la base de datos (Room)
            override fun loadFromDb(): Flow<UsuarioDto?> {
                return usuarioDao.getUsuarioWhereLogin(correo).map { usuario ->
                    if (usuario != null) {
                        val contrasennaValida = BCrypt.verifyer().verify(contrasenna.toCharArray(), usuario.contrasenna.toCharArray()).verified

                        if (contrasennaValida) {
                            usuario.toDto() // Contraseña verificada, devolver el usuario
                        } else {
                            null // Contraseña incorrecta, devolver null
                        }
                    } else {
                        null  // Usuario no encontrado
                    }
                }
            }

            // Llamar a la API para verificar si el correo existe
            override suspend fun fetchFromNetwork(): UsuarioWithTokenDto? {
                //si el login es correcto, guarda el token dentro de este metodo
                return usuariosRepository.postLogin(UsuarioLoginDto(correo, contrasenna))
            }

            // Guardar el resultado de la red en la base de datos
            override suspend fun saveNetworkResult(item: UsuarioWithTokenDto) {
                // Guardar el usuario en Room
                usuarioDao.cleanInsert(item.usuario.toEntity())
            }

            // Decidir si debemos realizar la llamada a la API (fetch)
            override fun shouldFetch(data: UsuarioDto?): Boolean {
                // Realizamos fetch si los datos locales son nulos
                return data == null
            }

            // Saber si el resultado vino de la API o no
            val fromNetwork: Boolean = false

        }.asFlow()
    }


    fun getUsuarioById(token: String, id: Long): Flow<Resource<out UsuarioDto?>> {
        return object : NetworkBoundResource<UsuarioDto, UsuarioDto>() {
            override fun loadFromDb(): Flow<UsuarioDto> {
                return usuarioDao.getUsuarioWhereId(id).map { it.toDto() }
            }

            override suspend fun fetchFromNetwork(): UsuarioDto? {
                return usuariosRepository.getUsuarioById(token, id)
            }

            override suspend fun saveNetworkResult(item: UsuarioDto) {
                usuarioDao.cleanInsert(item.toEntity())
            }

            override fun shouldFetch(data: UsuarioDto?): Boolean {
                // Siempre hacer fetch si los datos locales son nulos
                return data == null
            }
        }.asFlow()
    }

    fun getUsuarios(token: String): Flow<Resource<out List<UsuarioDto>?>> {
        return object : NetworkBoundResource<List<UsuarioDto>, List<UsuarioDto>>() {
            override fun loadFromDb(): Flow<List<UsuarioDto>> {
                return usuarioDao.getAll().map { it.map { dbUsuario -> dbUsuario.toDto() } }
            }

            override suspend fun fetchFromNetwork(): List<UsuarioDto>? {
                return usuariosRepository.getUsuarios(token)
            }

            override suspend fun saveNetworkResult(item: List<UsuarioDto> ) {
                usuarioDao.insertAll(item.map { it.toEntity() })
            }

            override fun shouldFetch(data: List<UsuarioDto>?): Boolean {
                // Siempre hacer fetch si los datos locales están vacíos
                return data.isNullOrEmpty()
            }
        }.asFlow()
    }
}

