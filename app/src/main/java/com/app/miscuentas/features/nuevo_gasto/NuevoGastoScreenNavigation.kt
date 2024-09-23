package com.app.miscuentas.features.nuevo_gasto

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.miscuentas.features.MainActivityViewModel
import com.app.miscuentas.features.splash.NavigateToSplash
import com.app.miscuentas.features.splash.SPLASH_ROUTE


const val NUEVO_GASTO_ROUTE = "NUEVO GASTO"
const val NUEVO_GASTO_ID_HOJA_PRINCIPAL = "idHojaPrincipal"

fun NavHostController.NavigateToNuevoGasto(idHojaPrincipal: Long){
    this.navigate( "$NUEVO_GASTO_ROUTE/$idHojaPrincipal")
}

fun NavGraphBuilder.nuevoGastoScreen(
    innerPadding: PaddingValues,
    navHostController: NavHostController,
    mainActivityViewModel: MainActivityViewModel
){
    composable(//composable de navegacion el cual recibe un argumentod e tipo Long
        route = "$NUEVO_GASTO_ROUTE/{$NUEVO_GASTO_ID_HOJA_PRINCIPAL}",//"nuevo_gasto" + "/{idHojaPrincipal}", //"$NUEVO_GASTO_ROUTE/$NUEVO_GASTO_ID_HOJA_PRINCIPAL",
        arguments = listOf(
            navArgument(NUEVO_GASTO_ID_HOJA_PRINCIPAL){
                type = NavType.LongType
            }
        )
    ) {
        mainActivityViewModel.setTitle(NUEVO_GASTO_ROUTE)
        // idHoja es la clave utilizada para pasar los datos
        it.arguments?.getLong(NUEVO_GASTO_ID_HOJA_PRINCIPAL)?.let { idHoja ->
            NuevoGasto(
                innerPadding = innerPadding,
                idHojaPrincipal = idHoja,
                onNavSplash = { navHostController.NavigateToSplash() },
                navigateUp = { navHostController.popBackStack() }
            )
        }

    }
}