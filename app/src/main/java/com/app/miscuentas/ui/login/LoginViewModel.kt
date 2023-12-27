package com.app.miscuentas.ui.login

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel(){

    //variables para ser trabajadas desde la clase
    private val _usuario = MutableLiveData("")
    private val _contrasenna = MutableLiveData("")
    private val _email = MutableLiveData("")
    private val _mensaje = MutableLiveData("")
    private val _login = MutableLiveData(false)

    //variables para ser usadas desde fuera de la clase
    val usuario : LiveData<String> = _usuario
    val contrasenna : LiveData<String> = _contrasenna
    val email : LiveData<String> = _email
    val mensaje : LiveData<String> = _mensaje
    val login : LiveData<Boolean> = _login

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
    fun getUsuario(): String = _usuario.value!!
    fun getContrasenna(): String = _contrasenna.value!!
    fun getEmail(): String = _email.value!!


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