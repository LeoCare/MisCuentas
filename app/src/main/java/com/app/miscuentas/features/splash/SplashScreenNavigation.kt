package com.app.miscuentas.features.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.inicio.NavigateToInicio
import com.app.miscuentas.features.login.NavigateToLogin

const val SPLASH_ROUTE = "SPLASH"

fun NavHostController.NavigateToSplash() {
    this.navigate(SPLASH_ROUTE) {
        popUpTo(SPLASH_ROUTE) { inclusive = true } // Elimina Splash de la pila
    }
}

fun NavGraphBuilder.splashScreen(
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
){
    composable(route = SPLASH_ROUTE) {
        mainActivityViewModel.setTitle(SPLASH_ROUTE)
        SplashScreen(
            onLoginNavigate = { navHostController.NavigateToLogin() } ,
            onInicioNavigate = { navHostController.NavigateToInicio() }
        )
    }
}
