package com.app.miscuentas.data.network.usuario


import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.domain.UsuarioCrearDto
import retrofit2.Response

class UsuariosRepository (private val usuariosService: UsuariosService ) {

    suspend fun putRegistro(usuarioCrearDto: UsuarioCrearDto): Usuario? {
        return usuariosService.putRegistro(usuarioCrearDto)
    }
}