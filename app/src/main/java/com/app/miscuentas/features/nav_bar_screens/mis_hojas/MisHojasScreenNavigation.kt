package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.gastos.NavigateToGastos
import com.app.miscuentas.features.nav_bar_screens.mis_gastos.MIS_GASTOS_ROUTE
import com.app.miscuentas.features.nav_bar_screens.participantes.NavigateToParticipantes
import com.app.miscuentas.features.nueva_hoja.NUEVA_HOJA_ROUTE
import com.app.miscuentas.features.splash.SPLASH_ROUTE

const val MIS_HOJAS_ROUTE = "MIS HOJAS"

fun NavHostController.NavigateToMisHojas(){
    this.navigate(MIS_HOJAS_ROUTE){
        popUpTo(route = MIS_GASTOS_ROUTE) { inclusive = true }
    }
}

fun NavGraphBuilder.misHojasScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
){
    composable( route = MIS_HOJAS_ROUTE ) {
        mainActivityViewModel.setTitle(MIS_HOJAS_ROUTE)
        MisHojasScreen(
            innerPadding,
            onNavGastos = { idHoja -> //lambda que nos permite pasarle un parametro a la navegacion
                navHostController.NavigateToGastos(idHoja)
            },
            onNavParticipantes = { idHoja ->
                navHostController.NavigateToParticipantes(idHoja)
            }
        )
    }
}