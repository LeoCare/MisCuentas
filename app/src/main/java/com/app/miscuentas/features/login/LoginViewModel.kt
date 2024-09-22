package com.app.miscuentas.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbUsuariosEntity
import com.app.miscuentas.data.model.dtoToEntityList
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.model.toEntityList
import com.app.miscuentas.data.network.BalancesService
import com.app.miscuentas.data.network.GastosService
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.PagosService
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,  // DATASTORE
    private val usuariosService: UsuariosService,
    private val participantesService: ParticipantesService,
    private val pagosService: PagosService,
    private val gastosService: GastosService,
    private val hojasService: HojasService,
    private val balancesService: BalancesService,
    private val tokenAuthenticator: TokenAuthenticator

) : ViewModel(){

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState


    //Metodos (para ser llamadas desde la vista) que asignan valor a las variables privadas.
    fun onUsuarioFieldChanged(usuario :String){
        _loginState.value = _loginState.value.copy(usuario = usuario)
    }
    fun onContrasennaFieldChanged(contrasenna: String) {
        _loginState.value = _loginState.value.copy(contrasenna = contrasenna)
    }
    fun onEmailFieldChanged(email: String) {
        _loginState.value = _loginState.value.copy(email = email)
    }
    fun onRegistroCheckChanged(registrarme: Boolean) {
        _loginState.value = _loginState.value.copy(registro = registrarme)
    }
    fun onMensajeChanged(mensaje: String) {
        _loginState.value = _loginState.value.copy(mensaje = mensaje)
    }
    fun onIsLoadingOkChanged(isLoading: Boolean) {
        _loginState.value = _loginState.value.copy(isLoading = isLoading)
    }
    fun onLoginOkChanged(loginOk: Boolean) {
        _loginState.value = _loginState.value.copy(loginOk = loginOk)
    }
    fun onIdRegistroChanged(idRegistro: Long) {
        _loginState.value = _loginState.value.copy(idRegistro = idRegistro)
    }



    /********* LOGIN **********/
    /**************************/

    /** Metodo para comprobar si existe ese usuario registrado (LOGIN) **/
    fun iniciarSesion() {
        val correo = _loginState.value.email
        val contrasenna = _loginState.value.contrasenna

        viewModelScope.launch {
            onIsLoadingOkChanged(true)
            try {
                val response = usuariosService.postLoginApi(UsuarioLoginDto(correo, contrasenna))
                if (response != null) {
                    // Guardar el usuario y los tokens
                    usuariosService.cleanInsert(response.usuario.toEntity())
                    tokenAuthenticator.saveTokens(response.accessToken, response.refreshToken)
                    // Actualizar el estado de la UI y cargar Room
                    limpiarYVolcarLogin(response.usuario)
                    onRegistroDataStoreChanged(response.usuario.idUsuario, response.usuario.nombre)
                } else {
                    onMensajeChanged("Correo o Contraseña incorrectos!")
                }
            } catch (e: Exception) {
                // Si hay un error de red, intentar cargar los datos locales
                val localUsuario = usuariosService.getUsuarioWhereLogin(correo).firstOrNull()
                if (localUsuario != null) {
                    val contrasennaValida = BCrypt.verifyer().verify(contrasenna.toCharArray(), localUsuario.contrasenna.toCharArray()).verified
                    if (contrasennaValida) {
                        // Inicio de sesión exitoso con datos locales
                        onRegistroDataStoreChanged(localUsuario.idUsuario, localUsuario.nombre)
                    } else {
                        onMensajeChanged("Error en Correo o Contraseña!")
                    }
                } else {
                    onMensajeChanged("Error en la red y no hay datos locales!")
                }
            } finally {
                onIsLoadingOkChanged(false)
            }
        }
    }

    //Solo si el login es exitoso desde la API y no desde ROOM:
    suspend fun limpiarYVolcarLogin(usuario: UsuarioDto){

        //hojas:
        val hojas = hojasService.getHojaByApi("id_usuario", usuario.idUsuario.toString())
        hojas?.forEach { hoja ->
            //participantes:
            val participantes = participantesService.getParticipantesBy("id_hoja", hoja.idHoja.toString())
            if (participantes != null) {
                hojasService.insertHojaConParticipantes(hoja.toEntity(), participantes.toEntityList())

                participantes.forEach{ participante ->
                    //gastos:
                    val gastos = gastosService.getGastoBy("id_participante", participante.idParticipante.toString())
                    if(gastos != null) gastosService.insertAllGastos(gastos.toEntityList())
                    //pagos:
                    val pagos = pagosService.getPagosBy("id_participante", participante.idParticipante.toString())
                    if(pagos != null) pagosService.insertAllPagos(pagos.toEntityList())
                }
            }
            //balances:
            val balances = balancesService.getBalanceByApi("id_hoja", hoja.idHoja.toString())
            if (balances != null) {
                balancesService.insertBalancesForHoja(hoja.toEntity(), balances.dtoToEntityList())
            }
        }
    }

    /** Actualiza datastore con los datos de login (LOGIN) **/
    suspend fun onRegistroDataStoreChanged(idRegistro: Long, usuario: String){
        dataStoreConfig.putRegistroPreference(usuario)
        dataStoreConfig.putIdRegistroPreference(idRegistro)
        onLoginOkChanged(true)
    }

    /********* REGISTRO **********/
    /*****************************/

    /** COMPRUEBO EXISTENCIA DEL CORREO EN AL API
     * Uso de NetworkBoundResource, el cual verifica si ya existe en la Api.
     * Actualiza Room con la API si fuera necesario.
     * **/
    fun inicioInsertRegistro() {
        val correo = _loginState.value.email

        viewModelScope.launch {
            onIsLoadingOkChanged(true)
            try {
                // Primero, intentamos verificar el correo con la API
                val usuarioDto = usuariosService.verifyCorreo(correo)
                if (usuarioDto != null) {
                    // El correo ya está registrado en la API
                    onMensajeChanged("El correo ya está registrado")
                } else {
                    // El correo no está registrado en la API, proceder con el registro
                    insertRegistroCall()
                }
            } catch (e: Exception) {
                // Si hay un error en la red, intentamos verificar en la base de datos local
                val localUsuarioEntity = usuariosService.getUsuarioWhereCorreo(correo).firstOrNull()
                if (localUsuarioEntity != null) {
                    // El correo ya está registrado en la base de datos local
                    onMensajeChanged("El correo ya está registrado (datos locales)")
                } else {
                    // El correo no está registrado en la API ni en local, proceder con el registro
                    insertRegistroCall()
                }
            } finally {
                onIsLoadingOkChanged(false)
            }
        }
    }


    /** LLAMADA A INSERTAR REGISTRO ç
     * Aqui se llega ya que ese correo no se encuentra en la BBDD.
     * Primero inserta desde la API y luego desde ROOM.**/
    suspend fun insertRegistroCall(){
        val nombre = _loginState.value.usuario
        val correo = _loginState.value.email
        val contrasenna = _loginState.value.contrasenna
        val perfil = "USER"
        val idRegistro: Long

        //Insert en API
        val usuarioApiOk = insertRegistroApi(contrasenna, correo, nombre, perfil)
        if (usuarioApiOk != null) {
            idRegistro = usuarioApiOk.usuario.idUsuario

            //Insert en Room
            val insertRoomOk = insertRegistroRoom(usuarioApiOk.usuario.contrasenna, correo, idRegistro, nombre, perfil)
            if (insertRoomOk) {
                onIdRegistroChanged(idRegistro)
                onRegistroDataStoreChanged(idRegistro, _loginState.value.usuario)
            } else  onMensajeChanged("Ese correo ya esta registrado!")
        }
    }

    /** INSERTAR REGISTRO (API) **/
    suspend fun insertRegistroApi(
        contrasenna: String,
        correo: String,
        nombre: String,
        perfil: String
    ): UsuarioWithTokenDto? {

        var result: UsuarioWithTokenDto? = null
        val usuarioCrearDto = UsuarioCrearDto(contrasenna, correo, nombre, perfil)

        try {
            val registroApi = usuariosService.putRegistroApi(usuarioCrearDto)
            if (registroApi != null){
                result = registroApi // insert OK
            }
        } catch (e: Exception) {
            onMensajeChanged("Imposible acceder al servidor, verifique la red!")
            onIsLoadingOkChanged( false)
            result = null // inserción NOK
        }

        return result
    }

    /** INSERTAR REGISTRO (ROOM) **/
    suspend fun insertRegistroRoom(
        contrasenna: String,
        correo: String,
        idRegistro: Long,
        nombre: String,
        perfil: String
    ): Boolean {

        val usuarioRoom = DbUsuariosEntity(idRegistro, nombre, correo , contrasenna, perfil)

        try {
            usuariosService.cleanInsert(usuarioRoom) //Insert en ROOM
            return true // insert OK
        } catch (e: Exception) {
            return false // inserción NOK
        }
    }


    /** BIOMETRIC **/
    init {
        // Observa los cambios en DataStore para comprobar el inicio por huella
        viewModelScope.launch {
            val inicioHuella = dataStoreConfig.getInicoHuellaPreference()
            val registrado = dataStoreConfig.getRegistroPreference()
            val token = dataStoreConfig.getAccessTokenPreference()

            if (registrado != null && token != null && inicioHuella == "SI") startBiometricAuthentication()
            else if (registrado != null && token != null) onLoginOkChanged(true)
            else  onLoginOkChanged(false)
        }
    }

    //METODOS PARA LA AUTENTICACION POR HUELLA
    // Función para iniciar la autenticación
    fun startBiometricAuthentication() {
        _loginState.value = _loginState.value.copy(
            biometricAuthenticationState = LoginState.BiometricAuthenticationState.Authenticating
        )
    }

    // Función si la autenticación es exitosa
    fun onBiometricAuthenticationSuccess() {
        _loginState.value = _loginState.value.copy(
            biometricAuthenticationState = LoginState.BiometricAuthenticationState.Authenticated
        )
    }

    // Función si la autenticación es fallida
    fun onBiometricAuthenticationFailed() {
        _loginState.value = _loginState.value.copy(
            biometricAuthenticationState = LoginState.BiometricAuthenticationState.AuthenticationFailed
        )
    }


    /** COMPROBACIONES **/
    // Metodos que comprueban la sintaxis del correo
    fun emailOk(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    // Metodos que comprueban la sintaxis de la contraseña
    fun contrasennaOk(contrasena: String): Boolean {
        if (contrasena.length < 6) {
            return false
        }

        var tieneNumero = false
        var tieneMayus = false
        var tieneMinus = false

        for (char in contrasena) {
            when {
                char.isDigit() -> tieneNumero = true
                char.isUpperCase() -> tieneMayus = true
                char.isLowerCase() -> tieneMinus = true
            }
        }
        return tieneNumero && tieneMayus && tieneMinus
    }

}