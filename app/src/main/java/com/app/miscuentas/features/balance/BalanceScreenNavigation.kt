package com.app.miscuentas.features.balance

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val BALANCE_ROUTE = "BALANCE"
const val BALANCE_ID_HOJA_A_MOSTRAR = "idHojaAMostrar"

fun NavHostController.NavigateToBalance(idHojaAMostrar: Long){
    this.navigate("$BALANCE_ROUTE/$idHojaAMostrar")
}

fun NavGraphBuilder.balanceScreen(
    innerPadding: PaddingValues?,
    navHostController: NavHostController
){
    composable(
        route = "$BALANCE_ROUTE/{$BALANCE_ID_HOJA_A_MOSTRAR}",
        arguments = listOf(
            navArgument(BALANCE_ID_HOJA_A_MOSTRAR) {
                type = NavType.LongType
            }
        )
    ) {
        it.arguments?.getLong(BALANCE_ID_HOJA_A_MOSTRAR)?.let { idHojaAMostrar ->
            BalanceScreen(
                innerPadding = innerPadding,
                idHojaAMostrar = idHojaAMostrar
            )
        }
    }
}