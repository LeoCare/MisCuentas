package com.app.miscuentas.features.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R


@Composable
fun SplashScreen(
    onLoginNavigate: () -> Unit,
    onInicioNavigate: () -> Unit,
   viewModel: SplashViewModel = hiltViewModel()
) {
    val splashState by viewModel.splashState.collectAsState()

    //Comprobacion de la version de la BBDD y de las Preferences
    LaunchedEffect(Unit){
        viewModel.checkAndClearDataStore()
    }

    //Luego de la comprobacion inicial del viewmodel:
    LaunchedEffect(splashState.continuar) {
           if(splashState.continuar) {
               if (splashState.autoInicio) onInicioNavigate()
               else onLoginNavigate()
           }
    }

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


}
