package com.app.miscuentas.features.inicio

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.nav_bar_screens.mis_hojas.NavigateToMisHojas
//import com.app.miscuentas.features.nav_bar_screens.NavigateToNavBar
import com.app.miscuentas.features.nueva_hoja.NavigateToNuevaHoja
import com.app.miscuentas.features.nuevo_gasto.NavigateToNuevoGasto
import com.app.miscuentas.features.splash.NavigateToSplash
import com.app.miscuentas.features.splash.SPLASH_ROUTE

const val INICIO_ROUTE = "INICIO"

fun NavHostController.NavigateToInicio() {
    this.navigate(INICIO_ROUTE) {
        popUpTo(SPLASH_ROUTE) { inclusive = true } // Elimina Splash de la pila
    }
}

fun NavGraphBuilder.inicioScreen(
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
){
    composable(route = INICIO_ROUTE) {
        mainActivityViewModel.setTitle(INICIO_ROUTE)
        Inicio(
            onNavSplash = { navHostController.NavigateToSplash() },
            onNavNuevaHoja = { navHostController.NavigateToNuevaHoja() },
            onNavMisHojas = { navHostController.NavigateToMisHojas() },
            onNavNuevoGasto = { idHojaPrincipal ->
                navHostController.NavigateToNuevoGasto(idHojaPrincipal)
            }
        )
    }
}