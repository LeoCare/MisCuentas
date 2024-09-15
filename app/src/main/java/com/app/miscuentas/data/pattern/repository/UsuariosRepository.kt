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
        return try {
            val response = webService.postLogin(usuarioLogin)
            if (response.isSuccessful) {
                val usuarioWithToken = response.body()

                // Guardar el token
                usuarioWithToken?.let {
                    tokenAuthenticator.saveToken(it.token)
                }

                usuarioWithToken
            } else {
                throw Exception("Error al hacer login: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    suspend fun getUsuarios(): List<UsuarioDto>? {
        return try {
            val response = webService.getUsuarios()
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener usuarios: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    suspend fun getUsuarioWhenData(column: String, query: String): List<UsuarioDto>? {
        return try {
            val response = webService.getUsuarioWhenData(column, query)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener los datos: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    suspend fun getUsuarioById(id: Long): UsuarioDto? {
        return try {
            val response = webService.getUsuarioById( id)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al obtener usuario por ID: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    suspend fun putUsuario(usuario: UsuarioDto): UsuarioDto? {
        return try {
            val response = webService.putUsuario(usuario)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al actualizar usuario: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

    suspend fun deleteUsuario(usuario: UsuarioDeleteDto): String? {
        return try {
            val response = webService.deleteUsuario(usuario)
            if (response.isSuccessful) {
                response.body()
            } else {
                throw Exception("Error al eliminar usuario: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception) {
            null
        }
    }

}
