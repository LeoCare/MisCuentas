package com.app.miscuentas.features.splash

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.inicio.NavigateToInicio
import com.app.miscuentas.features.login.NavigateToLogin
import com.app.miscuentas.features.navegacion.MisCuentasScreen

const val SPLASH_ROUTE = "splash"

fun NavHostController.NavigateToSplash() {
    this.navigate(SPLASH_ROUTE)
}

fun NavGraphBuilder.splashScreen(
    navHostController: NavHostController
){
    composable(route = SPLASH_ROUTE) {
        SplashScreen(
            onLoginNavigate = { navHostController.NavigateToLogin() },
            onInicioNavigate = { navHostController.NavigateToInicio() }
        )
    }
}
