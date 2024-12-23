package com.app.miscuentas.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.favre.lib.crypto.bcrypt.BCrypt
import com.app.miscuentas.data.auth.TokenAuthenticator
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.entitys.DbHojaCalculoEntity
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
import com.app.miscuentas.data.pattern.DataUpdates
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioLoginDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.math.log


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataUpdates: DataUpdates,
    private val dataStoreConfig: DataStoreConfig,
    private val usuariosService: UsuariosService
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
    fun onRepitaContrasennaFieldChanged(contrasenna: String) {
        _loginState.value = _loginState.value.copy(repitaContrasenna = contrasenna)
    }
    fun onEmailFieldChanged(email: String) {
        _loginState.value = _loginState.value.copy(email = email)
    }
    fun onVerifyCodigoRecupChanged(verificado: String){
        _loginState.value = _loginState.value.copy(verifyCodigoRecup = verificado)
    }
    fun onRegistroCheckChanged(registrarme: Boolean) {
        _loginState.value = _loginState.value.copy(registro = registrarme)
    }
    fun onRepetirPassChanged(repetir: Boolean) {
        _loginState.value = _loginState.value.copy(repetirPass = repetir)
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
                    // Actualizar el estado de la UI y cargar Room
                    onRegistroDataStoreChanged(response.usuario.idUsuario, response.usuario.nombre, response.usuario.correo)
                    dataUpdates.limpiarYVolcarLogin(response.usuario.idUsuario)
                    onLoginOkChanged(true)

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
                        onRegistroDataStoreChanged(localUsuario.idUsuario, localUsuario.nombre, localUsuario.correo)
                        onLoginOkChanged(true)
                    } else {
                        onMensajeChanged("Error en Correo o Contraseña!")
                    }
                } else {
                    onMensajeChanged("Correo y/o contraseña incorrectos!")
                }
            } finally {
                onIsLoadingOkChanged(false)
            }
        }
    }

    /** Actualiza datastore con los datos de login (LOGIN) **/
    suspend fun onRegistroDataStoreChanged(idRegistro: Long, usuario: String, correo: String){
        dataStoreConfig.putRegistroPreference(usuario)
        dataStoreConfig.putIdRegistroPreference(idRegistro)
        dataStoreConfig.putCorreoRegistroPreference(correo)
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
                val usuarioDto = usuariosService.verifyCorreoApi(correo)
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

    /** COMPRUEBO CODIGO DE RECUPERACION DESDE LA API **/
    fun comprobarCodigoRecup(correo: String, codigo: String) {
        viewModelScope.launch {
            onIsLoadingOkChanged(true)
            try {
                val esValido = usuariosService.verifyCodigoApi(correo, codigo)
                if (!esValido.isNullOrEmpty()) {
                    onEmailFieldChanged(correo)
                    onVerifyCodigoRecupChanged("OK")
                    onRepetirPassChanged(true)
                } else {
                    onRepetirPassChanged(false)
                    onVerifyCodigoRecupChanged("NOK")
                }
            } catch (e: Exception) {
                // Si hay un error en la red..
                onRepetirPassChanged(false)
                onVerifyCodigoRecupChanged("NOK")
            } finally {
                onIsLoadingOkChanged(false)
            }
        }
    }

    /** Envio de correo para recuperar la contraseña **/
    fun onEnviarCorreo(correo: String){
        viewModelScope.launch {
            try {
                // Primero, intentamos verificar el correo con la API
                val usuarioDto = usuariosService.verifyCorreoApi(correo)
                if (usuarioDto == null) {
                    onMensajeChanged("El correo no existe")
                } else {
                    //Update desde API
                    usuariosService.putUsuarioApi(usuarioDto)?.let {
                        onMensajeChanged("Codigo enviado por email!")
                    }
                }
            } catch (e: Exception) {
                // Si hay un error en la red
                onMensajeChanged("Problemas en la red")
            }
        }
    }

    /** ACTUALIZA LA CONTRASEÑA CON EL CODIGO RECIBIDO **/
    fun updatePass(){
        val correo = _loginState.value.email

        viewModelScope.launch {
            onIsLoadingOkChanged(true)
            try {
                // Primero, intentamos verificar el correo con la API
                val usuarioDto = usuariosService.verifyCorreoApi(correo)
                if (usuarioDto == null) {
                    onMensajeChanged("El correo no existe")
                    onIsLoadingOkChanged(false)
                } else {
                    usuarioDto.contrasenna = loginState.value.contrasenna
                    //Update desde API
                    usuariosService.putUsuarioNewPassApi(usuarioDto)?.let {
                        //Update desde ROOM
                        val actualizado = usuariosService.update(it.toEntity())
                        if(actualizado > 0)  iniciarSesion()
                        else onIsLoadingOkChanged(false)
                    }
                }
            } catch (e: Exception) {
                // Si hay un error en la red
                onMensajeChanged("Problemas en la red")
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
        val perfil = "ADMIN"
        val idRegistro: Long

        //Insert en API
        val usuarioApiOk = insertRegistroApi(contrasenna, correo, nombre, perfil)
        if (usuarioApiOk != null) {
            idRegistro = usuarioApiOk.usuario.idUsuario

            //Insert en Room
            val insertRoomOk = insertRegistroRoom(usuarioApiOk.usuario.contrasenna, correo, idRegistro, nombre, perfil)
            if (insertRoomOk) {
                onIdRegistroChanged(idRegistro)
                onRegistroDataStoreChanged(idRegistro, _loginState.value.usuario, correo)
                onLoginOkChanged(true)
            } else  onMensajeChanged("Ese correo ya esta registrado!")
        }else  onMensajeChanged("Problemas en el servidor. No es posible resitrarse en este momento, intentelo mas tarde!")
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

}