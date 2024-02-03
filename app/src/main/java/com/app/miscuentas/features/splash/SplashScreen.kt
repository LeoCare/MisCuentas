package com.app.miscuentas.features.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import kotlinx.coroutines.delay


@Composable
fun SplashScreen(onLoginNavigate: () -> Unit, onInicioNavigate: () -> Unit) {
    val viewModel: SplashViewModel = hiltViewModel()
    val spashState by viewModel.splashState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logologin),
            contentDescription = "Logo App")
        Text(
            text = "Cargando..",
            style = MaterialTheme.typography.titleLarge
        )
    }

    LaunchedEffect(Unit) {
        delay(2000) // Espera 2 segundos, por ejemplo
        if (spashState.autoInicio) onInicioNavigate()
        else onLoginNavigate()

    }
}
