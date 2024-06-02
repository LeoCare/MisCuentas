@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.miscuentas.features.navegacion

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.app.miscuentas.R
import com.app.miscuentas.features.gastos.GASTOS_ROUTE
import com.app.miscuentas.features.inicio.inicioScreen
import com.app.miscuentas.features.login.loginScreen
import com.app.miscuentas.features.nav_bar_screens.navBarScreen
import com.app.miscuentas.features.gastos.GastosScreen
import com.app.miscuentas.features.gastos.gastosScreen
import com.app.miscuentas.features.nav_bar_screens.mis_hojas.MIS_HOJAS_ROUTE
import com.app.miscuentas.features.nav_bar_screens.mis_hojas.MisHojasScreen
import com.app.miscuentas.features.nav_bar_screens.mis_hojas.misHojasScreen
import com.app.miscuentas.features.nav_bar_screens.participantes.PARTICIPANTES_ROUTE
import com.app.miscuentas.features.nav_bar_screens.participantes.ParticipantesScreen
import com.app.miscuentas.features.nav_bar_screens.participantes.participantesScreen
import com.app.miscuentas.features.nueva_hoja.nuevaHojaScreen
import com.app.miscuentas.features.nuevo_gasto.NavigateToNuevoGasto
import com.app.miscuentas.features.nuevo_gasto.nuevoGastoScreen
import com.app.miscuentas.features.splash.SPLASH_ROUTE
import com.app.miscuentas.features.splash.splashScreen
import com.app.miscuentas.util.Captura
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/** ************************INICIO************************** **/
/** NAVEGACION DEL MAIN ENTRE LAS DISTINTAS SCREEN DE LA APP **/
/** ******************************************************** **/
@Composable
fun AppNavHost(
    innerPadding: PaddingValues,
    scrollBehavior: TopAppBarScrollBehavior,
    navController: NavHostController
) {
    val activity = LocalContext.current as FragmentActivity

    //pila de Screen y valor actual
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: SPLASH_ROUTE
    val canNavigateBack = backStackEntry != null // Determinar si se puede navegar hacia atrás


    NavHost(
        navController = navController,
        startDestination = SPLASH_ROUTE
    ) {

        splashScreen(navController)
        loginScreen(navController)
        inicioScreen(navController)
        navBarScreen(navController, canNavigateBack)
        nuevaHojaScreen(navController)
        nuevoGastoScreen(navController)
    }
}
/** *************************FIN**************************** **/




/** ****************INICIO********************** **/
/** NAVEGACION DE LA BARRA INFERIOR EN MIS_HOJAS **/
/** ******************************************** **/
sealed class MisHojasScreen (var route: String, val icon: ImageVector, val title: String) {
    object MisHojas : MisHojasScreen(MIS_HOJAS_ROUTE, Icons.Default.Difference, "Mis Hojas")
    object MisGastos : MisHojasScreen("mis_gastos_route", Icons.Default.ShoppingCart, "Mis Gastos")
    object Participantes : MisHojasScreen(PARTICIPANTES_ROUTE, Icons.Default.Person, "Participantes")

}

//Composable de navegacion para la barra inferior en Mis_Hojas
@Composable
fun AppNavBar(
    innerPadding: PaddingValues,
    navControllerMisHojas: NavHostController,
    navHostController: NavHostController
) {
    NavHost(
        navController = navControllerMisHojas,
        startDestination = MIS_HOJAS_ROUTE
    ) {
        misHojasScreen(innerPadding, navControllerMisHojas, navHostController)
        gastosScreen(innerPadding, navControllerMisHojas, navHostController)
        //falta crear misGastosScreen(navControllerMisHojas, innerPadding)
        participantesScreen(innerPadding, navControllerMisHojas, navHostController)
    }
}


//Composable de los botones a mostrar
@Composable
fun BottomNavigationBar(navControllerMisHojas: NavController) {
    val items = listOf(
        MisHojasScreen.MisHojas,
        MisHojasScreen.MisGastos,
        MisHojasScreen.Participantes
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primary
    ) {
        val currentRoute =
            navControllerMisHojas.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { screen ->

            val isSelected =
                currentRoute?.contains(screen.route) ?: false //ruta seleccionada para resaltar
            val colorSeleccionado =
                if (isSelected) MaterialTheme.colorScheme.onBackground else Color.White //resaltar

            BottomNavigationItem(
                icon = {
                    Icon(
                        screen.icon,
                        contentDescription = null,
                        tint = colorSeleccionado
                    )
                },
                label = { Text(screen.title, color = colorSeleccionado) },
                selected = isSelected,
                onClick = {
                    if (screen == MisHojasScreen.MisGastos) {
                        // idHoja 0 para que no lo tenga en cuenta
                        val idHojaPredeterminado = 0
                        navControllerMisHojas.navigate("${screen.route}/$idHojaPredeterminado") {
                            // Evitar recrear la pantalla si ya está en la pila
                            popUpTo(navControllerMisHojas.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    } else {
                        navControllerMisHojas.navigate(screen.route) {
                            popUpTo(navControllerMisHojas.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.onSecondary
            )

        }
    }
}
/** *******************FIN********************** **/


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MiTopBar(
    context: Context,
    drawerState: DrawerState?,
    currentScreen: String,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    val activity = LocalContext.current as FragmentActivity
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    TopAppBar(
        title = {
            Text(
            currentScreen,
            fontSize = 25.sp
            )
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        navigationIcon = {
            if (canNavigateBack) { //muestra la flecha para volver atras
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            } else { //si no, muestra el menu lateral
                IconButton(
                    onClick = {
                        scope.launch { drawerState?.open() }
                    }
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menu")
                }
            }
        },
        actions = {
            IconButton(
                onClick = { Captura.capturarYEnviar(activity) }
            ) {
                Icon(Icons.Filled.Share, contentDescription = "Compartir")
            }
            IconButton(
                onClick = {}
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Informacion")
            }
        },
        scrollBehavior = scrollBehavior
    )
}


