package com.app.miscuentas.features.inicio

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.nav_bar_screens.mis_hojas.NavigateToMisHojas
//import com.app.miscuentas.features.nav_bar_screens.NavigateToNavBar
import com.app.miscuentas.features.nueva_hoja.NavigateToNuevaHoja
import com.app.miscuentas.features.nuevo_gasto.NavigateToNuevoGasto
import com.app.miscuentas.features.splash.NavigateToSplash

const val INICIO_ROUTE = "INICIO"

fun NavHostController.NavigateToInicio() {
    this.navigate(INICIO_ROUTE)
}

fun NavGraphBuilder.inicioScreen(
    navHostController: NavHostController
){
    composable(route = INICIO_ROUTE) {
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