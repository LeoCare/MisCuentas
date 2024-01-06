package com.app.miscuentas.ui.mis_hojas.ui

import android.annotation.SuppressLint
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.ui.MiTopBar
import com.app.miscuentas.ui.MisCuentasScreem

sealed class MisHojasScreen (val route: String, val icon: ImageVector, val title: String) {

    //Provisional!!
    object Hojas : MisHojasScreen("hojas", Icons.Default.Difference, "Hojas")
    object Gastos : MisHojasScreen("gastos", Icons.Default.ShoppingCart, "Gastos")
    object Participantes : MisHojasScreen("participantes", Icons.Default.Person, "Participantes")

}

//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
@Preview
@Composable
fun Prev(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreem.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreem.Mis_Hojas.name
    )
    MisHojas(
        currentScreen,
        navController
    )
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MisHojas(
    currentScreen: MisCuentasScreem, //para el topBar
    navController: NavHostController, //para el boton de 'ir atras'
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val navControllerMisHojas = rememberNavController()

    // Determinar si se puede navegar hacia atrás
    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
    val canNavigateBack = navBackStackEntry != null

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MiTopBar(
                currentScreen,
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = canNavigateBack,
                navigateUp = { navController.navigateUp() })
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        NavHost(navController = navControllerMisHojas, startDestination = MisHojasScreen.Hojas.route) {
            composable(MisHojasScreen.Hojas.route) { HojasScreen(navControllerMisHojas) }
            composable(MisHojasScreen.Gastos.route) { GastosScreen(navControllerMisHojas) }
            composable(MisHojasScreen.Participantes.route) { ParticipantesScreen(navControllerMisHojas) }
        }
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        MisHojasScreen.Hojas,
        MisHojasScreen.Gastos,
        MisHojasScreen.Participantes
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.onTertiaryContainer

    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        items.forEach { screen ->
            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null, tint = Color.White) },
                label = { Text(screen.title, color = Color.White) },
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        // Evitar recrear la pantalla si ya está en la pila
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun HojasScreen(navController: NavController) {
    // UI para HomeScreen
}

@Composable
fun GastosScreen(navController: NavController) {
    // UI para ProfileScreen
}

@Composable
fun ParticipantesScreen(navController: NavController) {
    // UI para ProfileScreen
}