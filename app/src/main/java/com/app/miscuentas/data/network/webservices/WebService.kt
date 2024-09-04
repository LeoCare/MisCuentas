package com.app.miscuentas.data.network.webservices

import com.app.miscuentas.data.model.Hoja
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.domain.UsuarioCrearDto
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    @POST("usuarios/registro")
    suspend fun putRegistro(
        @Body usuario: UsuarioCrearDto
    ): Response<Usuario?>


    @GET("usuarios/login")
    suspend fun getLogin(
        @Query("contrasenna") contrasenna: String,
        @Query("correo") correo: String
    ): List<Hoja>


    @GET("usuarios/{id}")
    suspend fun getUsuarioById(
        @Path("id") id: Long
    )

}