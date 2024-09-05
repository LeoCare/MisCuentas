package com.app.miscuentas.data.network.usuario


import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDeleteDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto

class UsuariosRepository (private val usuariosService: UsuariosService ) {

    // Registrar usuario (Registro)
    suspend fun putRegistro(usuarioCrearDto: UsuarioCrearDto): Usuario? {
        return usuariosService.putRegistro(usuarioCrearDto)
    }

    // Iniciar sesión de un usuario (Login)
    suspend fun postLogin(usuarioLoginDto: UsuarioLoginDto): UsuarioWithTokenDto? {
        return usuariosService.postLogin(usuarioLoginDto)
    }

    // Obtener la lista de todos los usuarios
    suspend fun getUsuarios(token: String): List<UsuarioDto>? {
        return usuariosService.getUsuarios(token)
    }

    // Obtener usuarios filtrados por una columna específica
    suspend fun getWhenData(token: String, column: String, query: String): List<UsuarioDto>? {
        return usuariosService.getWhenData(token, column, query)
    }

    // Obtener un usuario por ID
    suspend fun getUsuarioById(token: String, id: Long): UsuarioDto? {
        return usuariosService.getUsuarioById(token, id)
    }

    // Actualizar un usuario
    suspend fun putUsuario(token: String, usuarioDto: UsuarioDto): UsuarioDto? {
        return usuariosService.putUsuario(token, usuarioDto)
    }

    // Eliminar un usuario
    suspend fun deleteUsuario(token: String, usuarioDeleteDto: UsuarioDeleteDto): String? {
        return usuariosService.deleteUsuario(token, usuarioDeleteDto)
    }
}