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
import retrofit2.http.FormUrlEncoded
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
    ): Response<UsuarioWithTokenDto?>

    //Iniciar Login, obteniendo el token:
    @POST("usuarios/login")
    suspend fun postLogin(
        @Body usuarioLogin: UsuarioLoginDto
    ): Response<UsuarioWithTokenDto>

    //Lista de Usuarios:
    @GET("usuarios")
    suspend fun getUsuarios(): Response<List<UsuarioDto>>

    //Obtener un dato en concreto:
    @GET("usuarios/WhenData")
    suspend fun getUsuarioWhenData(
        @Query("c") column: String,
        @Query("q") query: String
    ): Response<List<UsuarioDto>>

    //Datos de un usuario segun id:
    @GET("usuarios/{id}")
    suspend fun getUsuarioById(
        @Path("id") id: Long
    ): Response<UsuarioDto>

    //Actualizar usuario:
    @PUT("usuarios")
    suspend fun putUsuario(
        @Body usuario: UsuarioDto
    ): Response<UsuarioDto>

    //Eliminar usuario:
    @DELETE("usuarios")
    suspend fun deleteUsuario(
        @Body usuario: UsuarioDeleteDto
    ): Response<String>
    /************************/


    /** SERVICIOS DE BALANCES **/
    // Obtener todos los balances
    @GET("balances")
    suspend fun getBalances(): Response<List<BalanceDto>>

    // Obtener un balance por ID
    @GET("balances/{id}")
    suspend fun getBalanceById(
        @Path("id") id: Long
    ): Response<BalanceDto>

    // Crear un nuevo balance
    @POST("balances")
    suspend fun postBalance(
        @Body balanceCrearDto: BalanceCrearDto
    ): Response<BalanceDto>

    // Actualizar un balance existente
    @PUT("balances")
    suspend fun putBalance(
        @Body balanceDto: BalanceDto
    ): Response<BalanceDto>

    // Eliminar un balance por ID
    @DELETE("balances/{id}")
    suspend fun deleteBalance(
        @Path("id") id: Long
    ): Response<String>
    /************************/


    /** SERVICIOS DE PAGOS **/
    // Obtener todos los pagos
    @GET("pagos")
    suspend fun getAllPagos(): Response<List<PagoDto>>

    // Obtener un pago por ID
    @GET("pagos/{id}")
    suspend fun getPagoById(
        @Path("id") id: Long
    ): Response<PagoDto>

    //Obtener un dato en concreto:
    @GET("pagos/WhenData")
    suspend fun getPagosWhenData(
        @Query("c") column: String,
        @Query("q") query: String
    ): Response<List<PagoDto>>

    // Crear un nuevo pago
    @POST("pagos")
    suspend fun createPago(
        @Body pagoCrearDto: PagoCrearDto
    ): Response<PagoDto>

    // Actualizar un pago existente
    @PUT("pagos")
    suspend fun updatePago(
        @Body pagoDto: PagoDto
    ): Response<PagoDto>

    // Eliminar un pago por ID
    @DELETE("pagos/{id}")
    suspend fun deletePago(
        @Path("id") id: Long
    ): Response<String>
    /************************/


    /** SERVICIOS DE GASTOS **/
    // Obtener todos los gastos
    @GET("gastos")
    suspend fun getAllGastos(): Response<List<GastoDto>>

    // Obtener un gasto por ID
    @GET("gastos/{id}")
    suspend fun getGastoById(
        @Path("id") id: Long
    ): Response<GastoDto>

    //Obtener un dato en concreto:
    @GET("gastos/WhenData")
    suspend fun getGastosWhenData(
        @Query("c") column: String,
        @Query("q") query: String
    ): Response<List<GastoDto>>

    // Crear un nuevo gasto
    @POST("gastos")
    suspend fun createGasto(
        @Body gastoCrearDto: GastoCrearDto
    ): Response<GastoDto>

    // Actualizar un gasto existente
    @PUT("gastos")
    suspend fun updateGasto(
        @Body gastoDto: GastoDto
    ): Response<GastoDto>

    // Eliminar un gasto por ID
    @DELETE("gastos/{id}")
    suspend fun deleteGasto(
        @Path("id") id: Long
    ): Response<String>
    /************************/

    /** SERVICIOS DE PARTICIPANTES **/
    // Obtener todos los participantes
    @GET("participantes")
    suspend fun getAllParticipantes(): Response<List<ParticipanteDto>>

    // Obtener un participante por ID
    @GET("participantes/{id}")
    suspend fun getParticipanteById(
        @Path("id") id: Long
    ): Response<ParticipanteDto>

    //Obtener un dato en concreto:
    @GET("participantes/WhenData")
    suspend fun getParticipanteWhenData(
        @Query("c") column: String,
        @Query("q") query: String
    ): Response<List<ParticipanteDto>>

    // Crear un nuevo participante
    @POST("participantes")
    suspend fun createParticipante(
        @Body participanteCrearDto: ParticipanteCrearDto
    ): Response<ParticipanteDto>

    // Actualizar un participante
    @PUT("participantes")
    suspend fun updateParticipante(
        @Body participanteDto: ParticipanteDto
    ): Response<ParticipanteDto>

    // Eliminar un participante por ID
    @DELETE("participantes/{id}")
    suspend fun deleteParticipante(
        @Path("id") id: Long
    ): Response<String>
    /************************/

    /** SERVICIOS DE HOJAS **/
    // Obtener todas las hojas
    @GET("hojas")
    suspend fun getAllHojas( ): Response<List<HojaDto>>

    // Obtener una hoja por ID
    @GET("hojas/{id}")
    suspend fun getHojaById(
        @Path("id") id: Long
    ): Response<HojaDto>

    //Obtener un dato en concreto:
    @GET("hojas/WhenData")
    suspend fun getHojasWhenData(
        @Query("c") column: String,
        @Query("q") query: String
    ): Response<List<HojaDto>>

    // Crear una nueva hoja
    @POST("hojas")
    suspend fun createHoja(
        @Body hojaCrearDto: HojaCrearDto
    ): Response<HojaDto>

    // Actualizar una hoja
    @PUT("hojas")
    suspend fun updateHoja(
        @Body hojaDto: HojaDto
    ): Response<HojaDto>

    // Eliminar una hoja por ID
    @DELETE("hojas/{id}")
    suspend fun deleteHoja(
        @Path("id") id: Long
    ): Response<String>
    /************************/

    /** SERVICIOS DE IMAGENES **/
    // Obtener todas las imágenes
    @GET("imagenes")
    suspend fun getAllImagenes(): Response<List<ImagenDto>>

    // Obtener una imagen por ID
    @GET("imagenes/{id}")
    suspend fun getImagenById(
        @Path("id") id: Long
    ): Response<ImagenDto>

    // Crear una nueva imagen
    @POST("imagenes")
    suspend fun createImagen(
        @Body imagenCrearDto: ImagenCrearDto
    ): Response<ImagenDto>

    // Actualizar una imagen
    @PUT("imagenes")
    suspend fun updateImagen(
        @Body imagenDto: ImagenDto
    ): Response<ImagenDto>

    // Eliminar una imagen por ID
    @DELETE("imagenes/{id}")
    suspend fun deleteImagen(
        @Path("id") id: Long
    ): Response<String>
    /************************/

    /** SERVICIOS DE LOS TIPOS **/
    // Obtener todos los tipos de perfil
    @GET("tipos/perfil")
    suspend fun getAllTipoPerfil(): Response<List<TipoPerfilDto>>

    // Obtener todos los tipos de balance
    @GET("tipos/balance")
    suspend fun getAllTipoBalance(): Response<List<TipoBalanceDto>>

    // Obtener todos los tipos de status
    @GET("tipos/status")
    suspend fun getAllTipoStatus(): Response<List<TipoStatusDto>>
}