package com.app.miscuentas.features.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.miscuentas.data.local.datastore.DataStoreConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    //private val sharedPreference: SharedPreferences,  //Prueba SHAREDPREFERENCES
    private val dataStoreConfig: DataStoreConfig     //Prueba DATASTORE
) : ViewModel(){

    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState


    //Metodos (para ser llamadas desde la vista) que asignan valor a las variables privadas.
    fun onUsuarioFieldChanged(usuario :String){
        _loginState.value = _loginState.value.copy(usuario = usuario)
    }
    fun onContrasennaFieldChanged(contrasenna: String) {
        _loginState.value = _loginState.value.copy(contrasena = contrasenna)
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
    fun onLoginOkChanged(registrado: Boolean) {
        _loginState.value = _loginState.value.copy(loginOk = registrado)

        viewModelScope.launch {
            dataStoreConfig.putRegistroPreference(registrado)
        }

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

    //PRUEBA DE SHAREDPREFERENCE, BORRAR!!
    //Este metodo guarda los datos de inicio
//    fun guardarLogin(usuario: String, contrasenna: String){
//        sharedPreference.edit().apply{
//            putString("usuario", usuario)
//            putString("contrasenna", contrasenna)
//            apply()
//        }
//    }
    //Si se guarda el usuario 'LEO' y la pass 'LEOleo1234' -> Iniciará la app evitando la screen de login
//    init {
//        val usuario = sharedPreference.getString("usuario", "")
//        val contrasenna = sharedPreference.getString("contrasenna", "")
//
//        if (usuario == "LEO" && contrasenna == "LEOleo1234" ) _loginState.value = _loginState.value.copy(loginOk = true)
//    }


    //PRUEBA DE DATASTORE
    init {
        // Observa los cambios en DataStore y actualiza el estado del loginOk
        viewModelScope.launch {
            val registrado = dataStoreConfig.getRegistroPreference() ?: false
            _loginState.value = _loginState.value.copy(loginOk = registrado)
        }
    }

}