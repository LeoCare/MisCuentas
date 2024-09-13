package com.app.miscuentas.data.pattern.repository


import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.pattern.webservices.WebService
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDeleteDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto
import javax.inject.Inject

class UsuariosRepository @Inject constructor(
    private val webService: WebService,
    private val tokenAuthenticator: TokenAuthenticator // Injecta el TokenAuthenticator
) {
    // Verificar si el correo existe
    suspend fun verifyCorreo(correo: String): UsuarioDto? {
        return try {
            val response = webService.verifyCorreo(correo)
            if (response.isSuccessful) {
                response.body() // Retorna el usuario si se encuentra
            } else {
                throw Exception("Error al verificar el correo: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    suspend fun putRegistro(usuario: UsuarioCrearDto): UsuarioWithTokenDto? {
        return try {
            val response = webService.putRegistro(usuario)
            if (response.isSuccessful) {
                val usuarioWithToken = response.body()
                // Guardar el token
                usuarioWithToken?.let {
                    tokenAuthenticator.saveToken(it.token)
                }
                usuarioWithToken
            } else {
                throw Exception("Error al registrar usuario: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    suspend fun postLogin(usuarioLogin: UsuarioLoginDto): UsuarioWithTokenDto? {
        val response = webService.postLogin(usuarioLogin)
        if (response.isSuccessful) {
            val usuarioWithToken = response.body()

            // Guardar el token
            usuarioWithToken?.let {
                tokenAuthenticator.saveToken(it.token)
            }

            return usuarioWithToken
        } else {
            throw Exception("Error al hacer login: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun getUsuarios(token: String): List<UsuarioDto>? {
        val response = webService.getUsuarios("Bearer $token")
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener usuarios: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun getWhenData(token: String, column: String, query: String): List<UsuarioDto>? {
        val response = webService.getWhenData("Bearer $token", column, query)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener los datos: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun getUsuarioById(token: String, id: Long): UsuarioDto? {
        val response = webService.getUsuarioById("Bearer $token", id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al obtener usuario por ID: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun putUsuario(token: String, usuario: UsuarioDto): UsuarioDto? {
        val response = webService.putUsuario("Bearer $token", usuario)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al actualizar usuario: ${response.code()} - ${response.message()}")
        }
    }

    suspend fun deleteUsuario(token: String, usuario: UsuarioDeleteDto): String? {
        val response = webService.deleteUsuario("Bearer $token", usuario)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw Exception("Error al eliminar usuario: ${response.code()} - ${response.message()}")
        }
    }


}
