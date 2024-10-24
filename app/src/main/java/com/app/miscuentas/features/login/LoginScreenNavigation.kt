package com.app.miscuentas.features.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.inicio.NavigateToInicio
import com.app.miscuentas.features.splash.SPLASH_ROUTE

const val LOGIN_ROUTE = "LOGIN"

fun NavHostController.NavigateToLogin() {
    this.navigate(LOGIN_ROUTE) {
        popUpTo(LOGIN_ROUTE) { inclusive = true } // Elimina Splash de la pila
    }
}

fun NavGraphBuilder.loginScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController
){
    composable(route = LOGIN_ROUTE) {
        Login(
            innerPadding,
            { navHostController.NavigateToInicio()  }
        )
    }
}

