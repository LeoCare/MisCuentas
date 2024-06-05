package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val MIS_GASTOS_ROUTE = "MIS GASTOS"

fun NavHostController.NavigateToMisGastos(){
    this.navigate(MIS_GASTOS_ROUTE)
}

fun NavGraphBuilder.misGastosScreen(
    innerPadding: PaddingValues?,
    navHostController: NavHostController
){
    composable(route = MIS_GASTOS_ROUTE){
        MisGastosScreen()
    }
}