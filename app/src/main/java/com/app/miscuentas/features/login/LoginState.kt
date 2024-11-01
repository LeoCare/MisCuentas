package com.app.miscuentas.features.login

data class LoginState(
    val usuario: String = "",
    val idRegistro: Long = 0,
    val contrasenna: String = "",
    val repitaContrasenna: String = "",
    val email: String = "",
    val mensaje: String = "",
    val verifyCodigoRecup: String = "",
    val registro: Boolean = false,
    val repetirPass: Boolean = false,
    val loginOk: Boolean = false,
    val isLoading: Boolean = false,
    val biometricAuthenticationState: BiometricAuthenticationState = BiometricAuthenticationState.Initial
) {
    sealed class BiometricAuthenticationState {
        object Initial : BiometricAuthenticationState()
        object Authenticating : BiometricAuthenticationState()
        object Authenticated : BiometricAuthenticationState()
        object AuthenticationFailed : BiometricAuthenticationState()
    }
}

