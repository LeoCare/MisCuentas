package com.app.miscuentas.features.nueva_hoja

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.nav_bar_screens.NavigateToNavBar

const val NUEVO_HOJA_ROUTE = "nueva_hoja"

fun NavHostController.NavigateToNuevaHoja(){
    this.navigate(NUEVO_HOJA_ROUTE)
}

fun NavGraphBuilder.nuevaHojaScreen(
    navHostController: NavHostController
){
    composable(route = NUEVO_HOJA_ROUTE) {
        NuevaHoja(
           // canNavigateBack,
            {navHostController.navigateUp()},
            onNavMisHojas = { navHostController.NavigateToNavBar() }
        )
    }
}