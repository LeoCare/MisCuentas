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
    private val _registro = MutableStateFlow(false)
    private val _login = MutableStateFlow(false)

    //variables para ser usadas desde fuera de la clase
    val usuario: StateFlow<String> = _usuario
    val contrasenna: StateFlow<String> = _contrasenna
    val email: StateFlow<String> = _email
    val mensaje: StateFlow<String> = _mensaje
    val registro: StateFlow<Boolean> = _registro
    val login: StateFlow<Boolean> = _login

    //Metodos (para ser llamadas desde la vista) que asignan valor a las variables privadas.
    fun onUsuarioFieldChanged(usuario :String){
        _usuario.value = usuario
    }
    fun onContrasennaFieldChanged(contrasenna :String) {
        _contrasenna.value = contrasenna
    }
    fun onEmailFieldChanged(email :String){
        _email.value = email
    }
    fun onRegistroCheckChanged(registrarme :Boolean){
        _registro.value = registrarme
    }


    //Metodo que se ejecuta al hacer click al boton del login
    fun mensajeLoginClick(){
        if (_usuario.value == "") _mensaje.value = "Falta Usuario"
        else if( !contrasennaOk() ) _mensaje.value = "Pass con 6 digitos minimo (num, mayusc. y minusc.)"

        else if(_registro.value){ //Si el check de registrar esta marcado....
            if( !emailOk() ) _mensaje.value = "Email incorrecto"
            else {
                _mensaje.value = ""
                _login.value = true
            }
        }

        else {
            _mensaje.value = ""
            _login.value = true
        }
    }

    //Metodos que comprueban la sintaxis del correo y la contraseña
    private fun emailOk() : Boolean = Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()

    private fun contrasennaOk() : Boolean {
        if (_contrasenna.value.length < 6) {
            return false
        }

        var tieneNumero = false
        var tieneMayus = false
        var tieneMinus = false

        for (char in _contrasenna.value) {
            when {
                char.isDigit() -> tieneNumero = true
                char.isUpperCase() -> tieneMayus = true
                char.isLowerCase() -> tieneMinus = true
            }
        }
        return tieneNumero && tieneMayus && tieneMinus
    }
}