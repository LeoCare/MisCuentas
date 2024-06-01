package com.app.miscuentas.features.inicio

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.login.NavigateToLogin
import com.app.miscuentas.features.navegacion.MisCuentasScreen
import com.app.miscuentas.features.splash.NavigateToSplash
import com.app.miscuentas.features.splash.SplashScreen

const val INICIO_ROUTE = "inicio"

fun NavHostController.NavigateToInicio() {
    this.navigate(INICIO_ROUTE)
}

fun NavGraphBuilder.inicioScreen(
    navHostController: NavHostController
){
    composable(route = INICIO_ROUTE) {
        Inicio(
            { navHostController.navigateUp() },
            onNavSplash = { navHostController.NavigateToSplash() },
            onNavNuevaHoja = { navController.navigate(MisCuentasScreen.NuevaHoja.route) },
            onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.route) },
            onNavNuevoGasto = { idHoja ->
                navController.navigate(MisCuentasScreen.NuevoGasto.route + "/$idHoja")
            }
        )
    }
}