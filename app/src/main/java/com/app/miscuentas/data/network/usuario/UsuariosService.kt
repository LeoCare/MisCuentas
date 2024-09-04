package com.app.miscuentas.data.network.usuario

import android.content.ContentValues
import android.util.Log
import com.app.miscuentas.data.model.Hoja
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.data.network.webservices.WebService
import com.app.miscuentas.domain.UsuarioCrearDto
import retrofit2.Response

class UsuariosService (private val webService: WebService) {

    suspend fun putRegistro(usuario: UsuarioCrearDto): Usuario? {
        val response = webService.putRegistro(usuario)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al registrar usuario: ${response.code()} - ${response.message()}")
        }
    }
}