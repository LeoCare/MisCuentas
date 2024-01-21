package com.app.miscuentas.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.app.miscuentas.viewmodel.states.LoginState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel(){

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
}