package com.app.miscuentas.ui.login.ui

import android.util.Patterns
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LoginViewModel : ViewModel(){

    //variables para ser trabajadas desde la clase
    private val _usuario = MutableStateFlow("")
    private val _contrasenna = MutableStateFlow("")
    private val _email = MutableStateFlow("")
    private val _mensaje = MutableStateFlow("")
    private val _login = MutableStateFlow(false)

    //variables para ser usadas desde fuera de la clase
    val usuario : StateFlow<String> = _usuario
    val contrasenna : StateFlow<String> = _contrasenna
    val email : StateFlow<String> = _email
    val mensaje : StateFlow<String> = _mensaje
    val login : StateFlow<Boolean> = _login

    //Metodos que asignan valor a las variables privadas.
    fun onUsuarioFieldChanged(usuario :String){
        _usuario.value = usuario
    }
    fun onContrasennaFieldChanged(contrasenna :String) {
        _contrasenna.value = contrasenna
    }
    fun onEmailFieldChanged(email :String){
        _email.value = email
    }

    //Propiedades
    fun getUsuario(): String = _usuario.value
    fun getContrasenna(): String = _contrasenna.value
    fun getEmail(): String = _email.value


    //Metodo que se ejecuta al hacer click al boton del login
    fun MensajeLoginClick(){
        if (getUsuario() == "") _mensaje.value = "Falta Usuario"
        else if( !ContrasennaOk() ) _mensaje.value = "Error en la contraseña"
        else if( !EmailOk() ) _mensaje.value = "Email incorrecto"
        else {
            _mensaje.value = ""
            _login.value = true
        }
    }

    //Metodos que comprueban la sintaxis del correo y la contraseña
    fun EmailOk() : Boolean = Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()

    fun ContrasennaOk() : Boolean {
        if (getContrasenna().length < 8) {
            return false
        }

        var tieneNumero = false
        var tieneMayus = false
        var tieneMinus = false

        for (char in getContrasenna()) {
            when {
                char.isDigit() -> tieneNumero = true
                char.isUpperCase() -> tieneMayus = true
                char.isLowerCase() -> tieneMinus = true
            }
        }
        return tieneNumero && tieneMayus && tieneMinus
    }
}