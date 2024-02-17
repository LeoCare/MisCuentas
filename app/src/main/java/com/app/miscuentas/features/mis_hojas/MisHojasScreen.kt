package com.app.miscuentas.features.mis_hojas

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.features.navegacion.AppNavBar
import com.app.miscuentas.features.navegacion.BottomNavigationBar
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.navegacion.MisCuentasScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi


//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
@Preview
@Composable
fun Prev(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreen.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreen.MisHojas.name
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
    val canNavigateBack = navBackStackEntry != null // Determinar si se puede navegar hacia atrás
    MisHojas(
        currentScreen,
        canNavigateBack,
        {navController.navigateUp()}
    )
}

/** Composable principal de la Screen **/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisHojas(
    currentScreen: MisCuentasScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val navControllerMisHojas = rememberNavController()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MiTopBar(
                context,
                null,
                currentScreen,
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = canNavigateBack,
                navigateUp = { navigateUp() }
            )
        },
        bottomBar = { BottomNavigationBar(navControllerMisHojas) },
        content = { innerPadding -> AppNavBar(innerPadding,navControllerMisHojas) }
    )
}