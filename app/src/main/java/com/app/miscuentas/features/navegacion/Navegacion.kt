@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.miscuentas.features.navegacion

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.miscuentas.R
import com.app.miscuentas.features.inicio.Inicio
import com.app.miscuentas.features.login.Login
import com.app.miscuentas.features.mis_hojas.MisHojas
import com.app.miscuentas.features.mis_hojas.nav_bar_screen.GastosScreen
import com.app.miscuentas.features.mis_hojas.nav_bar_screen.HojasScreen
import com.app.miscuentas.features.mis_hojas.nav_bar_screen.ParticipantesScreen
import com.app.miscuentas.features.nueva_hoja.NuevaHoja
import com.app.miscuentas.features.nuevo_gasto.NuevoGasto
import com.app.miscuentas.features.splash.SplashScreen
import com.app.miscuentas.util.Captura
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/** ************************INICIO************************** **/
/** NAVEGACION DEL MAIN ENTRE LAS DISTINTAS SCREEN DE LA APP **/
/** ******************************************************** **/
enum class MisCuentasScreen(@StringRes val title: Int){
    Splash(title = R.string.splash),
    Login(title = R.string.login),
    Inicio(title = R.string.inicio),
    NuevaHoja(title = R.string.nueva_hoja),
    MisHojas(title = R.string.mis_hojas),
    NuevoGasto(title = R.string.nuevo_gasto)
}

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val activity = LocalContext.current as FragmentActivity

    //pila de Screen y valor actual
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreen.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreen.Splash.name
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
    val canNavigateBack = navBackStackEntry != null // Determinar si se puede navegar hacia atrás


    NavHost(
        navController = navController,
        startDestination = MisCuentasScreen.Splash.name
    ) {

        composable(MisCuentasScreen.Splash.name) {
            SplashScreen(
                activity,
                onLoginNavigate = { navController.navigate(MisCuentasScreen.Login.name) },
                onInicioNavigate = { navController.navigate(MisCuentasScreen.Inicio.name) }
            )
        }
        composable(MisCuentasScreen.Login.name) {
            Login(
                { navController.navigate(MisCuentasScreen.Inicio.name) }
            )
        }
        composable(MisCuentasScreen.Inicio.name) {
            Inicio(
                currentScreen,
                {navController.navigateUp()},
                onNavSplash = { navController.navigate(MisCuentasScreen.Splash.name) },
                onNavNuevoGasto = { navController.navigate(MisCuentasScreen.NuevoGasto.name)},
                onNavNuevaHoja = { navController.navigate(MisCuentasScreen.NuevaHoja.name) },
                onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.name) }
            )
        }
        composable(MisCuentasScreen.NuevaHoja.name) {
            NuevaHoja(
                currentScreen,
                canNavigateBack,
                {navController.navigateUp()},
                onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.name) }
            )
        }
        composable(MisCuentasScreen.MisHojas.name) {
            MisHojas(
                currentScreen,
                canNavigateBack,
                {navController.navigateUp()}
            )
        }
        composable(MisCuentasScreen.NuevoGasto.name) {
            NuevoGasto(
                currentScreen,
                canNavigateBack,
                {navController.navigateUp()}
            )
        }
    }
}
/** *************************FIN**************************** **/




/** ****************INICIO********************** **/
/** NAVEGACION DE LA BARRA INFERIOR EN MIS_HOJAS **/
/** ******************************************** **/
sealed class MisHojasScreen (val route: String, val icon: ImageVector, val title: String) {

    object Hojas : MisHojasScreen("hojas", Icons.Default.Difference, "Hojas")
    object Gastos : MisHojasScreen("gastos", Icons.Default.ShoppingCart, "Gastos")
    object Participantes : MisHojasScreen("participantes", Icons.Default.Person, "Participantes")

}

//Composable de navegacion para la barra inferior en Mis_Hojas
@Composable
fun AppNavBar(innerPadding: PaddingValues, navControllerMisHojas: NavHostController) {

    NavHost(
        navController = navControllerMisHojas,
        startDestination = MisHojasScreen.Gastos.route
    ) {
        composable(MisHojasScreen.Hojas.route) { HojasScreen(innerPadding) }
        composable(MisHojasScreen.Gastos.route) { GastosScreen(innerPadding) }
        composable(MisHojasScreen.Participantes.route) { ParticipantesScreen() }
    }
}


//Composable de los botones a mostrar
@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        MisHojasScreen.Hojas,
        MisHojasScreen.Gastos,
        MisHojasScreen.Participantes
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.primaryContainer
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route //ruta seleccionada para resaltar
            val colorSeleccionado = if (isSelected) MaterialTheme.colorScheme.tertiary else Color.White //resaltar

            BottomNavigationItem(
                icon = { Icon(screen.icon, contentDescription = null, tint =  colorSeleccionado) },
                label = { Text(screen.title,color =  colorSeleccionado) },
                selected = isSelected,
                onClick = {
                    navController.navigate(screen.route) {
                        // Evitar recrear la pantalla si ya está en la pila
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                selectedContentColor = MaterialTheme.colorScheme.tertiary
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
    currentScreen: MisCuentasScreen,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    val activity = LocalContext.current as FragmentActivity

    TopAppBar(
        title = {
            Text(
                stringResource(currentScreen.title),
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
                        imageVector = Icons.Filled.ArrowBack,
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
        }
    )
}


