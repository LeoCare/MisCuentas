package com.app.miscuentas.features.login

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.inicio.NavigateToInicio

const val LOGIN_ROUTE = "login"

fun NavHostController.NavigateToLogin() {
    this.navigate(LOGIN_ROUTE)
}

fun NavGraphBuilder.loginScreen(
    navHostController: NavHostController
){
    composable(route = LOGIN_ROUTE) {
        Login(
            { navHostController.NavigateToInicio()  }
        )
    }
}