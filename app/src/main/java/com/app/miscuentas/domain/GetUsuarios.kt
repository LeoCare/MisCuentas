package com.app.miscuentas.domain

import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.data.pattern.UsuariosRepository
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDeleteDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto

class GetUsuarios(
    private val usuariosRepository: UsuariosRepository
) {

    // Registrar usuario (Registro)
    suspend fun putRegistro(usuarioCrearDto: UsuarioCrearDto): Usuario?{
        return usuariosRepository.putRegistroApi(usuarioCrearDto)
    }

    // Iniciar sesión de un usuario (Login)
    suspend fun postLogin(usuarioLoginDto: UsuarioLoginDto): UsuarioWithTokenDto? {
        return usuariosRepository.postLoginApi(usuarioLoginDto)
    }

    // Obtener la lista de todos los usuarios
    suspend fun getUsuarios(token: String): List<UsuarioDto>? {
        return usuariosRepository.getUsuariosApi(token)
    }

    // Obtener usuarios filtrados por una columna específica
    suspend fun getWhenData(token: String, column: String, query: String): List<UsuarioDto>? {
        return usuariosRepository.getWhenDataApi(token, column, query)
    }

    // Obtener un usuario por ID
    suspend fun getUsuarioById(token: String, id: Long): UsuarioDto? {
        return usuariosRepository.getUsuarioByIdApi(token, id)
    }

    // Actualizar un usuario
    suspend fun putUsuario(token: String, usuarioDto: UsuarioDto): UsuarioDto? {
        return usuariosRepository.putUsuarioApi(token, usuarioDto)
    }

    // Eliminar un usuario
    suspend fun deleteUsuario(token: String, usuarioDeleteDto: UsuarioDeleteDto): String? {
        return usuariosRepository.deleteUsuarioApi(token, usuarioDeleteDto)
    }

}
