package com.app.miscuentas.features.login

import android.database.sqlite.SQLiteConstraintException
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import com.app.miscuentas.data.local.dbroom.dbRegistros.DbRegistroDao
import com.app.miscuentas.data.local.repository.RegistroRepository
import com.app.miscuentas.domain.model.Registro
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val dataStoreConfig: DataStoreConfig,  // DATASTORE
    private val registroRepository: RegistroRepository
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
    fun onLoginOkChanged(loginOk: Boolean) {
        _loginState.value = _loginState.value.copy(loginOk = loginOk)

    }

    //Metodos que comprueban la sintaxis del correo y la contraseña
    fun emailOk(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

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

    //Metodo para comprobar si existe ese usuario registrado
    fun getRegistro(){
        val nombre = _loginState.value.usuario
        val contrasenna = _loginState.value.contrasenna
        var registro: Registro?

        viewModelScope.launch {
             withContext(Dispatchers.IO) {
                registro = registroRepository.getRegistro(nombre, contrasenna).firstOrNull()
                 if  (registro != null){
                     onRegistroDataStoreChanged(_loginState.value.usuario) //si existe, actualiza el dataStore con el nombre
                 }
                 else _loginState.value = _loginState.value.copy(mensaje = "Usuario o Contraseña incorrectos!")
            }
        }
    }

    suspend fun onRegistroDataStoreChanged(usuario: String){
            dataStoreConfig.putRegistroPreference(usuario)
            onLoginOkChanged(true)

    }


    //1_LLAMADA A INSERTAR REGISTRO
    fun insertRegistroCall(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val insertOk = insertRegistro()

                if (insertOk) onRegistroDataStoreChanged(_loginState.value.usuario)
                else _loginState.value = _loginState.value.copy(mensaje = "Ese correo ya esta registrado!")
            }
        }
    }

    //2_INSERTAR REGISTRO
    private suspend fun insertRegistro(): Boolean {
        val nombre = _loginState.value.usuario
        val correo = _loginState.value.email
        val contrasenna = _loginState.value.contrasenna

        val registro = Registro(0, nombre, correo, contrasenna)

        return try {
            val existeRegistro = registroRepository.getRegistroExist(correo).firstOrNull()
            if (existeRegistro == null) {
                registroRepository.insertAll(registro)
                true // insert OK
            }
            else false

        } catch (e: Exception) {
           false // inserción NOK
        }
    }


    // DATASTORE
    init {
        // Observa los cambios en DataStore para comprobar el inicio por huella
        viewModelScope.launch {
            val inicioHuella = dataStoreConfig.getInicoHuellaPreference()
            val registrado = dataStoreConfig.getRegistroPreference()

            if (registrado != null && inicioHuella == "SI") startBiometricAuthentication()
            else if (registrado != null) _loginState.value = _loginState.value.copy(loginOk = true)
            else  _loginState.value = _loginState.value.copy(loginOk = false)
        }
    }

    //METODOS PARA LA AUTENTICACION POR HUELLA (BIOMETRIC)
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