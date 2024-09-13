package com.app.miscuentas.features.login

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.model.Participante
import com.app.miscuentas.data.model.Usuario
import com.app.miscuentas.data.model.toEntity
import com.app.miscuentas.data.network.BalancesService
import com.app.miscuentas.data.network.GastosService
import com.app.miscuentas.data.network.HojasService
import com.app.miscuentas.data.network.ParticipantesService
import com.app.miscuentas.data.network.UsuariosService
import com.app.miscuentas.data.pattern.repository.Resource
import com.app.miscuentas.domain.dto.UsuarioCrearDto
import com.app.miscuentas.domain.dto.UsuarioDto
import com.app.miscuentas.domain.dto.UsuarioWithTokenDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,  // DATASTORE
    private val usuariosService: UsuariosService,
    private val participantesService: ParticipantesService,
    private val pagosService: ParticipantesService,
    private val gastosService: GastosService,
    private val hojasService: HojasService,
    private val balancesService: BalancesService

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
     fun iniciarSesion(){
        val correo = _loginState.value.email
        val contrasenna = _loginState.value.contrasenna

        viewModelScope.launch {
             withContext(Dispatchers.IO) {
                 usuariosService.getUsuarioByLogin(correo, contrasenna).collect { resource ->
                     when (resource) {
                         is Resource.Loading -> {
                             onIsLoadingOkChanged(true)
                         }
                         is Resource.Success -> {
                             // Usuario autenticado con éxito y token guardado
                             val usuario = resource.data
                             if (usuario == null) {
                                 onMensajeChanged("Correo o Contraseña incorrectos!")
                                 onIsLoadingOkChanged(false)
                             } else {// Si el logeo es correcto..
                                 //VOLCAMOS TODOS LOS DATOS PERTENECIENTES A ESE USUARIO:
                                 // Si el login fue exitoso desde la API, limpia y vuelca datos
                                 if (resource.fromNetwork) {  // Solo si viene de la API
                                     limpiarYVolcarLogin(usuario)
                                 }
                                 onIsLoadingOkChanged( false)
                                 //Esto permitira la navegacion:
                                 onRegistroDataStoreChanged(usuario.idUsuario, usuario.nombre) //si existe, actualiza el dataStore con el nombre
                             }
                         }
                         is Resource.Error -> {// Si el logeo no es correcto..
                             // Mostrar el mensaje de error
                             Log.e("LoginError", resource.message ?: "Error desconocido")
                             onMensajeChanged("Error en Correo o Contraseña!")
                             onIsLoadingOkChanged( false)
                         }
                     }
                 }
            }
        }
    }

    //Solo si el login es exitoso desde la API y no desde ROOM:
    suspend fun limpiarYVolcarLogin(usuario: UsuarioDto){
        val token = dataStoreConfig.getTokenPreference()
        val idRegistrado = usuario.idUsuario

        // Obtener datos desde la API y guardarlos en Room
        val hojas = token?.let { hojasService.getAllHojas(it) }?.filter { it.idUsuario == idRegistrado }
//        val participantes = participantesService.getParticipantes(usuarioId)
//        val pagos = apiService.getPagos(usuarioId)
//        val gastos = apiService.getGastos(usuarioId)


//        // Limpia e Insertar los datos en Room
        hojas?.forEach { hojasService.insertHojaCalculo(it.toEntity()) }
//        usuarioDao.insert(participantes.map { it.toEntity() })
//        pagosDao.insert(pagos.map { it.toEntity() })
//        gastosDao.insert(gastos.map { it.toEntity() })

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
            usuariosService.getRegistroByCorreo(correo).collect { resource ->
                when (resource) {
                    is Resource.Loading -> { // Muestra un spinner o indica que la operación está en curso
                        onIsLoadingOkChanged(true)
                    }

                    is Resource.Success -> { // Verifica si el correo existe o es nulo, tanto en ROOM como en la API:
                        val existeCorreoRegistro = resource.data

                        if (existeCorreoRegistro == null) {
                            // Si no existe, llamar a la función para insertar el registro
                            insertRegistroCall()
                        } else {// Si ya existe el correo:
                            onMensajeChanged("El correo ya está registrado")
                            onIsLoadingOkChanged( false)
                        }
                    }

                    is Resource.Error -> {// No lo devuelven ni Room ni la API
                        insertRegistroCall()
                        onMensajeChanged(resource.message ?: "Error desconocido")
                        onIsLoadingOkChanged( false)
                    }
                }
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
        var idRegistro: Long = 0

        //Insert en API
        val usuarioApiOk = insertRegistroApi(contrasenna, correo, nombre, perfil)
        if (usuarioApiOk != null) {
            idRegistro = usuarioApiOk.usuario.idUsuario

            //Insert en Room
            val insertRoomOk = insertRegistroRoom(usuarioApiOk.usuario.contrasenna, correo, idRegistro, nombre, perfil)
            if (insertRoomOk) {
                onRegistroDataStoreChanged(_loginState.value.idRegistro, _loginState.value.usuario)
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

        val usuarioRoom = Usuario(contrasenna, correo, idRegistro, nombre, perfil)

        try {
            val participante = Participante(0, usuarioRoom.nombre, usuarioRoom.correo)
            usuariosService.insertUsuarioConParticipantes(usuarioRoom, participante ) //Insert en ROOM
            //onIdRegistroChanged(idRegistroRoom) //guardado el id del insert del registro
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
            val token = dataStoreConfig.getTokenPreference()

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