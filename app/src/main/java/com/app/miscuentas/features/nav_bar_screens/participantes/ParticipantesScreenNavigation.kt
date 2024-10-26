package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.gastos.GASTOS_ID_HOJA_A_MOSTRAR
import com.app.miscuentas.features.login.LOGIN_ROUTE
import com.app.miscuentas.features.nav_bar_screens.mis_gastos.MIS_GASTOS_ROUTE
import com.app.miscuentas.features.nav_bar_screens.mis_hojas.MIS_HOJAS_ROUTE
import com.app.miscuentas.features.splash.SPLASH_ROUTE

const val PARTICIPANTES_ROUTE = "PARTICIPANTES"
const val PARTICIPANTES_ID_HOJA_A_MOSTRAR = "idHojaAMostrar"

fun NavHostController.NavigateToParticipantes(idHojaAMostrar: Long){
    this.navigate("$PARTICIPANTES_ROUTE/$idHojaAMostrar")
}

fun NavGraphBuilder.participantesScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
){
    composable(
        route = "$PARTICIPANTES_ROUTE/{$PARTICIPANTES_ID_HOJA_A_MOSTRAR}",
        arguments = listOf(
            navArgument(PARTICIPANTES_ID_HOJA_A_MOSTRAR) {
                type = NavType.LongType
            }
        )
    ) {
        it.arguments?.getLong(PARTICIPANTES_ID_HOJA_A_MOSTRAR)?.let { idHojaAMostrar ->
            mainActivityViewModel.setTitle(PARTICIPANTES_ROUTE)
            ParticipantesScreen(
                innerPadding = innerPadding,
                idHojaAMostrar = idHojaAMostrar,
            )
        }
    }
}