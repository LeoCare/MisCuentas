package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.nav_bar_screens.participantes.PARTICIPANTES_ROUTE

const val MIS_GASTOS_ROUTE = "MIS GASTOS"

fun NavHostController.NavigateToMisGastos(){
    this.navigate(MIS_GASTOS_ROUTE)
}

fun NavGraphBuilder.misGastosScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
){
    composable(route = MIS_GASTOS_ROUTE){
        mainActivityViewModel.setTitle(MIS_GASTOS_ROUTE)
        MisGastosScreen(innerPadding)
    }
}