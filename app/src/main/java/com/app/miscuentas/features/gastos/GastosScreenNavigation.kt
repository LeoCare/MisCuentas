package com.app.miscuentas.features.gastos

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.miscuentas.features.nuevo_gasto.NavigateToNuevoGasto

const val GASTOS_ROUTE = "gastos"
const val GASTOS_ID_HOJA_A_MOSTRAR = "idHojaAMostrar"

fun NavHostController.NavigateToGastos(idHojaAMostrar: Long){
    this.navigate("$GASTOS_ROUTE/$idHojaAMostrar")
}

fun NavGraphBuilder.gastosScreen(
    innerPadding: PaddingValues?,
    navControllerMisHojas: NavHostController,
    navHostController: NavHostController
){
    composable(
        route = "$GASTOS_ROUTE/{$GASTOS_ID_HOJA_A_MOSTRAR}",
        arguments = listOf(
            navArgument(GASTOS_ID_HOJA_A_MOSTRAR) {
                type = NavType.LongType
            }
        )
    ) {
        it.arguments?.getLong(GASTOS_ID_HOJA_A_MOSTRAR)?.let { idHojaAMostrar ->
            GastosScreen(
                innerPadding,
                idHojaAMostrar,
                onNavNuevoGasto = { idHoja ->
                    navHostController.NavigateToNuevoGasto(idHoja)}
            )
        }
    }
}