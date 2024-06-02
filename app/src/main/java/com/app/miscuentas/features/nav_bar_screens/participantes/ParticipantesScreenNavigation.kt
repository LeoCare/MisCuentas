package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable

const val PARTICIPANTES_ROUTE = "participantes"

fun NavHostController.NavigateToParticipantes(){
    this.navigate(PARTICIPANTES_ROUTE)
}

fun NavGraphBuilder.participantesScreen(
    innerPadding: PaddingValues?,
    navHostController: NavHostController
){
    composable(route = PARTICIPANTES_ROUTE) {
        ParticipantesScreen { navHostController.navigateUp() }
    }
}