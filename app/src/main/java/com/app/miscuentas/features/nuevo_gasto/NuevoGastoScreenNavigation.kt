package com.app.miscuentas.features.nuevo_gasto

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val NUEVO_GASTO_ROUTE = "NUEVO GASTO"
const val NUEVO_GASTO_ID_HOJA_PRINCIPAL = "idHojaPrincipal"

fun NavHostController.NavigateToNuevoGasto(idHojaPrincipal: Long){
    this.navigate( "$NUEVO_GASTO_ROUTE/$idHojaPrincipal")
}

fun NavGraphBuilder.nuevoGastoScreen(
    navHostController: NavHostController
){
    composable(//composable de navegacion el cual recibe un argumentod e tipo Long
        route = "$NUEVO_GASTO_ROUTE/{$NUEVO_GASTO_ID_HOJA_PRINCIPAL}",//"nuevo_gasto" + "/{idHojaPrincipal}", //"$NUEVO_GASTO_ROUTE/$NUEVO_GASTO_ID_HOJA_PRINCIPAL",
        arguments = listOf(
            navArgument(NUEVO_GASTO_ID_HOJA_PRINCIPAL){
                type = NavType.LongType
            }
        )
    ) {
        // idHoja es la clave utilizada para pasar los datos
        it.arguments?.getLong(NUEVO_GASTO_ID_HOJA_PRINCIPAL)?.let { idHoja ->
            NuevoGasto(
                idHoja,
                {navHostController.navigateUp()}
            )
        }

    }
}