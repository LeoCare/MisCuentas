package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.app.miscuentas.features.navegacion.MisHojasScreen

const val PARTICIPANTES_ROUTE = "participantes"

fun NavHostController.NavigateToParticipantes(){
    this.navigate(PARTICIPANTES_ROUTE)
}

fun NavGraphBuilder.participantesScreen(
    innerPadding: PaddingValues?,
    navControllerMisHojas: NavHostController,
    navHostController: NavHostController
){
    composable(route = PARTICIPANTES_ROUTE) {
        ParticipantesScreen()
    }
}