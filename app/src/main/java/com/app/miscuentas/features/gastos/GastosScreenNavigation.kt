package com.app.miscuentas.features.gastos

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.balance.NavigateToBalance
import com.app.miscuentas.features.login.LOGIN_ROUTE
import com.app.miscuentas.features.nuevo_gasto.NavigateToNuevoGasto
import com.app.miscuentas.features.splash.SPLASH_ROUTE

const val GASTOS_ROUTE = "GASTOS"
const val GASTOS_ID_HOJA_A_MOSTRAR = "idHojaAMostrar"

fun NavHostController.NavigateToGastos(idHojaAMostrar: Long){
    this.navigate("$GASTOS_ROUTE/$idHojaAMostrar"){
        popUpTo("$GASTOS_ROUTE/$idHojaAMostrar") { inclusive = true } // Elimina Splash de la pila
    }
}

fun NavGraphBuilder.gastosScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
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
            mainActivityViewModel.setTitle(GASTOS_ROUTE)
            GastosScreen(
                innerPadding = innerPadding,
                idHojaAMostrar = idHojaAMostrar,
                onNavNuevoGasto = { idHoja ->
                    navHostController.NavigateToNuevoGasto(idHoja)
                                  },
                onNavBalance = { idHoja ->
                    navHostController.NavigateToBalance(idHoja)
                }
            )
        }
    }
}