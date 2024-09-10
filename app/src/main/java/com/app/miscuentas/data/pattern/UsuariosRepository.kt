package com.app.miscuentas.data.pattern


import androidx.room.Transaction
import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.pattern.dao.DbUsuarioDao
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.data.model.toDto
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityWithUsuario
import com.app.miscuentas.data.model.toLogin
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDeleteDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UsuariosRepository @Inject constructor(
    private val usuarioDao: DbUsuarioDao,
    private val usuariosService: UsuariosService
) {

    /*****************/
    /** ROOM (local)**/
    /*****************/

    //  Registro de usuario (Room))
    suspend fun insert(usuario: Usuario) {
        usuarioDao.insert(usuario.toEntity())
    }

    //  Actualizar usuario (Room)
    suspend fun update(usuario: Usuario) = usuarioDao.update(usuario.toEntity())

    //  Eliminar usuario (Room)
    suspend fun delete(usuario: Usuario) = usuarioDao.delete(usuario.toEntity())

    //  Obtener un registro por ID (Room)
    fun getRegistroWhereId(idUsuario: Long): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereId(idUsuario).map { it?.toDomain() }

    //  Obtener usuario por login (Room)
    fun getUsuarioWhereLogin(nombre: String, contrasenna: String): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereLogin(nombre, contrasenna).map { it?.toDomain() }

    //  Obtener usuario por correo (Room)
    fun getUsuarioWhereCorreo(correo: String): Flow<Usuario?> =
        usuarioDao.getUsuarioWhereCorreo(correo).map { it?.toDomain() }

    //  Insertar usuario con participantes (Room)
    @Transaction
    suspend fun insertUsuarioConParticipantes(
        usuario: Usuario,
        participante: Participante
    ): Long {
        val dbUsuario = usuario.toEntity()
        val dbParticipante = participante.toEntityWithUsuario(0) // Inicialmente sin idRegistro
        return usuarioDao.insertUsuarioConParticipante(dbUsuario, dbParticipante)
    }
    /**********/


    /**********/
    /** API **/
    /**********/

    // Registrar usuario (API)
    suspend fun putRegistroApi(usuarioCrearDto: UsuarioCrearDto): UsuarioWithTokenDto? {
        return usuariosService.putRegistro(usuarioCrearDto)
    }

    // Iniciar sesión de un usuario (API)
    suspend fun postLoginApi(usuarioLoginDto: UsuarioLoginDto): UsuarioWithTokenDto? {
        return usuariosService.postLogin(usuarioLoginDto)
    }

    // Obtener la lista de todos los usuarios (API)
    suspend fun getUsuariosApi(token: String): List<UsuarioDto>? {
        return usuariosService.getUsuarios(token)
    }

    // Obtener usuarios filtrados por una columna específica (API)
    suspend fun getWhenDataApi(token: String, column: String, query: String): List<UsuarioDto>? {
        return usuariosService.getWhenData(token, column, query)
    }

    // Obtener un usuario por ID (API)
    suspend fun getUsuarioByIdApi(token: String, id: Long): UsuarioDto? {
        return usuariosService.getUsuarioById(token, id)
    }

    // Actualizar un usuario (API)
    suspend fun putUsuarioApi(token: String, usuarioDto: UsuarioDto): UsuarioDto? {
        return usuariosService.putUsuario(token, usuarioDto)
    }

    // Eliminar un usuario (API)
    suspend fun deleteUsuarioApi(token: String, usuarioDeleteDto: UsuarioDeleteDto): String? {
        return usuariosService.deleteUsuario(token, usuarioDeleteDto)
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
                return usuariosService.verifyCorreo(correo)
            }

            // Guardar el resultado de la red en la base de datos
            override suspend fun saveNetworkResult(item: UsuarioDto) {
                usuarioDao.insert(item.toEntity())
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
                return usuarioDao.getUsuarioWhereLogin(correo, contrasenna).map { it?.toDto() }

            }

            // Llamar a la API para verificar si el correo existe
            override suspend fun fetchFromNetwork(): UsuarioWithTokenDto? {
                //si el login es correcto, guarda el token dentro de este metodo
                return usuariosService.postLogin(UsuarioLoginDto(correo, contrasenna))
            }

            // Guardar el resultado de la red en la base de datos
            override suspend fun saveNetworkResult(item: UsuarioWithTokenDto) {
                // Guardar el usuario en Room
                usuarioDao.insert(item.usuario.toEntity())
            }

            // Decidir si debemos realizar la llamada a la API (fetch)
            override fun shouldFetch(data: UsuarioDto?): Boolean {
                // Realizamos fetch si los datos locales son nulos
                return data == null
            }

        }.asFlow()
    }


        fun getUsuarioById(token: String, id: Long): Flow<Resource<out UsuarioDto?>> {
            return object : NetworkBoundResource<UsuarioDto, UsuarioDto>() {
                override fun loadFromDb(): Flow<UsuarioDto> {
                    return usuarioDao.getUsuarioWhereId(id).map { it.toDto() }
                }

                override suspend fun fetchFromNetwork(): UsuarioDto? {
                    return usuariosService.getUsuarioById(token, id)
                }

                override suspend fun saveNetworkResult(item: UsuarioDto) {
                    usuarioDao.insert(item.toEntity())
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
                    return usuariosService.getUsuarios(token)
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
