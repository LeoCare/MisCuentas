package com.app.miscuentas.domain

import com.app.miscuentas.data.model.Hoja
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.data.network.usuario.UsuariosRepository

class GetUsuarios(private val usuariosRepository: UsuariosRepository) {
    suspend fun putRegistro(usuarioCrearDto: UsuarioCrearDto): Usuario?{
        return usuariosRepository.putRegistro(usuarioCrearDto)
    }

}