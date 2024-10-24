package com.app.miscuentas.features.balance

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.splash.SPLASH_ROUTE

const val BALANCE_ROUTE = "BALANCE"
const val BALANCE_ID_HOJA_A_MOSTRAR = "idHojaAMostrar"

fun NavHostController.NavigateToBalance(idHojaAMostrar: Long){
    this.navigate("$BALANCE_ROUTE/$idHojaAMostrar"){
        popUpTo("$BALANCE_ROUTE/$idHojaAMostrar") { inclusive = true } // Elimina Splash de la pila
    }
}

fun NavGraphBuilder.balanceScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
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
            mainActivityViewModel.setTitle(BALANCE_ROUTE)
            BalanceScreen(
                innerPadding = innerPadding,
                idHojaAMostrar = idHojaAMostrar
            )
        }
    }
}