package com.app.miscuentas.features.nav_bar_screens

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val NAV_BAR_ROUTE = "nav_bar"

fun NavHostController.NavigateToNavBar(){
    this.navigate(NAV_BAR_ROUTE)
}

fun NavGraphBuilder.navBarScreen(
    navHostController: NavHostController,
    canNavigateBack: Boolean,
){
    composable(route = NAV_BAR_ROUTE) {
        NavBar(
            canNavigateBack,
            navHostController
        )
    }
}