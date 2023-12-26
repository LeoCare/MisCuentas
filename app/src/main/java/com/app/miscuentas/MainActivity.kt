package com.app.miscuentas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.app.miscuentas.ui.inicio.ui.ScaffoldContent
import com.app.miscuentas.ui.login.LoginContent
import com.app.miscuentas.ui.login.LoginViewModel
import com.app.miscuentas.ui.theme.MisCuentasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MisCuentasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginContent(LoginViewModel())
                }
            }
        }
    }
}