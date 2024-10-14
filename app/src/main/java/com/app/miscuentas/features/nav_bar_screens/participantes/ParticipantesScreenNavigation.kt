package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.splash.SPLASH_ROUTE

const val PARTICIPANTES_ROUTE = "PARTICIPANTES"

fun NavHostController.NavigateToParticipantes(){
    this.navigate(PARTICIPANTES_ROUTE)
}

fun NavGraphBuilder.participantesScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
){
    composable(route = PARTICIPANTES_ROUTE) {
        mainActivityViewModel.setTitle(PARTICIPANTES_ROUTE)
        ParticipantesScreen(innerPadding)
    }
}