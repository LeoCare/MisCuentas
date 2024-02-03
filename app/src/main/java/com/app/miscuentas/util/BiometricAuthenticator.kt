package com.app.miscuentas.util

import android.content.Context
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.app.miscuentas.features.login.LoginState
import java.util.concurrent.Executor

class BiometricAuthenticator(
    private val context: Context
) {

    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val biometricManager = BiometricManager.from(context)
    private lateinit var biometricPrompt: BiometricPrompt


    fun isBiometricDisponible(): LoginState.BiometricAuthenticationState{
        return when(biometricManager.canAuthenticate(BIOMETRIC_STRONG)){
            BiometricManager.BIOMETRIC_SUCCESS -> LoginState.BiometricAuthenticationState.Initial
            else -> LoginState.BiometricAuthenticationState.AuthenticationFailed
        }
    }

    fun promptBiometricAuth(
        fragmentActivity: FragmentActivity,
        onSuccess: (result: BiometricPrompt.AuthenticationResult) -> Unit,
        onFailure: () -> Unit,
        onError: (errorString: String) -> Unit
    ){
        when (isBiometricDisponible()){
            LoginState.BiometricAuthenticationState.AuthenticationFailed -> {
                onError("Huella no disponible")
                return
            }
            else -> Unit
        }

        biometricPrompt = BiometricPrompt(
            fragmentActivity,
            object: BiometricPrompt.AuthenticationCallback(){
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    onFailure()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess(result)
                }
            }
        )

        promptInfo = BiometricPrompt.PromptInfo.Builder()

            .setTitle("Inicio con Huella")
            .setSubtitle("Inicia la applicacion usando la huella digital.")
            .setNegativeButtonText("Cancelar")
            .build()
        biometricPrompt.authenticate(promptInfo)

    }
}