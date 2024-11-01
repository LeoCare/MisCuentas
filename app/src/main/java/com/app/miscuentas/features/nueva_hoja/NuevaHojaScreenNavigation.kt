package com.app.miscuentas.features.nueva_hoja

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.nav_bar_screens.mis_hojas.NavigateToMisHojas
import com.app.miscuentas.features.splash.SPLASH_ROUTE

//import com.app.miscuentas.features.nav_bar_screens.NavigateToNavBar

const val NUEVA_HOJA_ROUTE = "NUEVA HOJA"

fun NavHostController.NavigateToNuevaHoja(){
    this.navigate(NUEVA_HOJA_ROUTE)
}

fun NavGraphBuilder.nuevaHojaScreen(
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
){
    composable(route = NUEVA_HOJA_ROUTE) {
        mainActivityViewModel.setTitle(NUEVA_HOJA_ROUTE)
        NuevaHoja(
            onNavMisHojas = { navHostController.NavigateToMisHojas() }
        )
    }
}