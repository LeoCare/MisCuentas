package com.app.miscuentas.features.nav_bar_screens

import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.features.navegacion.AppNavBar
import com.app.miscuentas.features.navegacion.BottomNavigationBar
import com.app.miscuentas.features.navegacion.MiTopBar


//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
//@Preview
//@Composable
//fun Prev(){
//    val navController = rememberNavController()
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentScreen = MisCuentasScreen.valueOf(
//        backStackEntry?.destination?.route ?: MisCuentasScreen.MisHojas.name
//    )
//    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
//    val canNavigateBack = navBackStackEntry != null // Determinar si se puede navegar hacia atrás
//    MisHojas(
//        currentScreen,
//        canNavigateBack,
//        {navController.navigateUp()}
//    )
//}

/** Composable principal de la Screen **/
@Composable
fun NavBar(
    canNavigateBack: Boolean,
    navController: NavHostController
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
                "MIS HOJAS",
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = canNavigateBack,
                navigateUp = { navControllerMisHojas.navigateUp() }
            )
        },
        bottomBar = { BottomNavigationBar(navControllerMisHojas) },
        content = { innerPadding -> AppNavBar(innerPadding, navControllerMisHojas, navController) }
    )
}