package com.app.miscuentas.data.pattern.webservices

import com.app.miscuentas.data.dto.TipoBalanceDto
import com.app.miscuentas.data.dto.TipoPerfilDto
import com.app.miscuentas.data.dto.TipoStatusDto
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.domain.dto.BalanceCrearDto
import com.app.miscuentas.domain.dto.BalanceDto
import com.app.miscuentas.domain.dto.GastoCrearDto
import com.app.miscuentas.domain.dto.GastoDto
import com.app.miscuentas.domain.dto.HojaCrearDto
import com.app.miscuentas.domain.dto.HojaDto
import com.app.miscuentas.domain.dto.ImagenCrearDto
import com.app.miscuentas.domain.dto.ImagenDto
import com.app.miscuentas.domain.dto.PagoCrearDto
import com.app.miscuentas.domain.dto.PagoDto
import com.app.miscuentas.domain.dto.ParticipanteCrearDto
import com.app.miscuentas.domain.dto.ParticipanteDto
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDeleteDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface WebService {

    /** SERVICIOS DE USUARIOS **/
    //Registrar usuario:
    @GET("usuarios/verify")
    suspend fun verifyCorreo(
        @Query("correo") correo: String
    ): Response<UsuarioDto>

    //Registrar usuario:
    @POST("usuarios/registro")
    suspend fun putRegistro(
        @Body usuario: UsuarioCrearDto
    ): Response<Usuario?>

    //Iniciar Login, obteniendo el token:
    @POST("usuarios/login")
    suspend fun postLogin(
        @Body usuarioLogin: UsuarioLoginDto
    ): Response<UsuarioWithTokenDto>

    //Lista de Usuarios:
    @GET("usuarios")
    suspend fun getUsuarios(
        @Header("Authorization") token: String
    ): Response<List<UsuarioDto>>

    //Obtener un dato en concreto:
    @GET("usuarios/WhenData")
    suspend fun getWhenData(
        @Header("Authorization") token: String,
        @Query("c") column: String,
        @Query("q") query: String
    ): Response<List<UsuarioDto>>

    //Datos de un usuario segun id:
    @GET("usuarios/{id}")
    suspend fun getUsuarioById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<UsuarioDto>

    //Actualizar usuario:
    @PUT("usuarios")
    suspend fun putUsuario(
        @Header("Authorization") token: String,
        @Body usuario: UsuarioDto
    ): Response<UsuarioDto>

    //Eliminar usuario:
    @DELETE("usuarios")
    suspend fun deleteUsuario(
        @Header("Authorization") token: String,
        @Body usuario: UsuarioDeleteDto
    ): Response<String>
    /************************/


    /** SERVICIOS DE BALANCES **/
    // Obtener todos los balances
    @GET("balances")
    suspend fun getBalances(
        @Header("Authorization") token: String
    ): Response<List<BalanceDto>>

    // Obtener un balance por ID
    @GET("balances/{id}")
    suspend fun getBalanceById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<BalanceDto>

    // Crear un nuevo balance
    @POST("balances")
    suspend fun postBalance(
        @Header("Authorization") token: String,
        @Body balanceCrearDto: BalanceCrearDto
    ): Response<BalanceDto>

    // Actualizar un balance existente
    @PUT("balances")
    suspend fun putBalance(
        @Header("Authorization") token: String,
        @Body balanceDto: BalanceDto
    ): Response<BalanceDto>

    // Eliminar un balance por ID
    @DELETE("balances/{id}")
    suspend fun deleteBalance(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<String>
    /************************/


    /** SERVICIOS DE PAGOS **/
    // Obtener todos los pagos
    @GET("pagos")
    suspend fun getAllPagos(
        @Header("Authorization") token: String
    ): Response<List<PagoDto>>

    // Obtener un pago por ID
    @GET("pagos/{id}")
    suspend fun getPagoById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<PagoDto>

    // Crear un nuevo pago
    @POST("pagos")
    suspend fun createPago(
        @Header("Authorization") token: String,
        @Body pagoCrearDto: PagoCrearDto
    ): Response<PagoDto>

    // Actualizar un pago existente
    @PUT("pagos")
    suspend fun updatePago(
        @Header("Authorization") token: String,
        @Body pagoDto: PagoDto
    ): Response<PagoDto>

    // Eliminar un pago por ID
    @DELETE("pagos/{id}")
    suspend fun deletePago(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<String>
    /************************/


    /** SERVICIOS DE GASTOS **/
    // Obtener todos los gastos
    @GET("gastos")
    suspend fun getAllGastos(
        @Header("Authorization") token: String
    ): Response<List<GastoDto>>

    // Obtener un gasto por ID
    @GET("gastos/{id}")
    suspend fun getGastoById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<GastoDto>

    // Crear un nuevo gasto
    @POST("gastos")
    suspend fun createGasto(
        @Header("Authorization") token: String,
        @Body gastoCrearDto: GastoCrearDto
    ): Response<GastoDto>

    // Actualizar un gasto existente
    @PUT("gastos")
    suspend fun updateGasto(
        @Header("Authorization") token: String,
        @Body gastoDto: GastoDto
    ): Response<GastoDto>

    // Eliminar un gasto por ID
    @DELETE("gastos/{id}")
    suspend fun deleteGasto(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<String>
    /************************/

    /** SERVICIOS DE PARTICIPANTES **/
    // Obtener todos los participantes
    @GET("participantes")
    suspend fun getAllParticipantes(
        @Header("Authorization") token: String
    ): Response<List<ParticipanteDto>>

    // Obtener un participante por ID
    @GET("participantes/{id}")
    suspend fun getParticipanteById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ParticipanteDto>

    // Crear un nuevo participante
    @POST("participantes")
    suspend fun createParticipante(
        @Header("Authorization") token: String,
        @Body participanteCrearDto: ParticipanteCrearDto
    ): Response<ParticipanteDto>

    // Actualizar un participante
    @PUT("participantes")
    suspend fun updateParticipante(
        @Header("Authorization") token: String,
        @Body participanteDto: ParticipanteDto
    ): Response<ParticipanteDto>

    // Eliminar un participante por ID
    @DELETE("participantes/{id}")
    suspend fun deleteParticipante(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<String>
    /************************/

    /** SERVICIOS DE HOJAS **/
    // Obtener todas las hojas
    @GET("hojas")
    suspend fun getAllHojas(
        @Header("Authorization") token: String
    ): Response<List<HojaDto>>

    // Obtener una hoja por ID
    @GET("hojas/{id}")
    suspend fun getHojaById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<HojaDto>

    // Crear una nueva hoja
    @POST("hojas")
    suspend fun createHoja(
        @Header("Authorization") token: String,
        @Body hojaCrearDto: HojaCrearDto
    ): Response<HojaDto>

    // Actualizar una hoja
    @PUT("hojas")
    suspend fun updateHoja(
        @Header("Authorization") token: String,
        @Body hojaDto: HojaDto
    ): Response<HojaDto>

    // Eliminar una hoja por ID
    @DELETE("hojas/{id}")
    suspend fun deleteHoja(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<String>
    /************************/

    /** SERVICIOS DE IMAGENES **/
    // Obtener todas las imágenes
    @GET("imagenes")
    suspend fun getAllImagenes(
        @Header("Authorization") token: String
    ): Response<List<ImagenDto>>

    // Obtener una imagen por ID
    @GET("imagenes/{id}")
    suspend fun getImagenById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ImagenDto>

    // Crear una nueva imagen
    @POST("imagenes")
    suspend fun createImagen(
        @Header("Authorization") token: String,
        @Body imagenCrearDto: ImagenCrearDto
    ): Response<ImagenDto>

    // Actualizar una imagen
    @PUT("imagenes")
    suspend fun updateImagen(
        @Header("Authorization") token: String,
        @Body imagenDto: ImagenDto
    ): Response<ImagenDto>

    // Eliminar una imagen por ID
    @DELETE("imagenes/{id}")
    suspend fun deleteImagen(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<String>
    /************************/

    /** SERVICIOS DE LOS TIPOS **/
    // Obtener todos los tipos de perfil
    @GET("tipos/perfil")
    suspend fun getAllTipoPerfil(
        @Header("Authorization") token: String
    ): Response<List<TipoPerfilDto>>

    // Obtener todos los tipos de balance
    @GET("tipos/balance")
    suspend fun getAllTipoBalance(
        @Header("Authorization") token: String
    ): Response<List<TipoBalanceDto>>

    // Obtener todos los tipos de status
    @GET("tipos/status")
    suspend fun getAllTipoStatus(
        @Header("Authorization") token: String
    ): Response<List<TipoStatusDto>>
}