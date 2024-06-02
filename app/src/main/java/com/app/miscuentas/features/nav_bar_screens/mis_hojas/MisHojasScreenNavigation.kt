package com.app.miscuentas.features.nav_bar_screens.mis_hojas

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.gastos.NavigateToGastos

const val MIS_HOJAS_ROUTE = "mis_hojas"

fun NavHostController.NavigateToMisHojas(){
    this.navigate(MIS_HOJAS_ROUTE)
}

fun NavGraphBuilder.misHojasScreen(
    innerPadding: PaddingValues?,
    navControllerMisHojas: NavHostController,
    navHostController: NavHostController
){
    composable( route = MIS_HOJAS_ROUTE ) {
        MisHojasScreen(
            innerPadding,
            onNavGastos = { idHoja -> //lambda que nos permite pasarle un parametro a la navegacion
                navControllerMisHojas.NavigateToGastos(idHoja)
            }
        )
    }
}