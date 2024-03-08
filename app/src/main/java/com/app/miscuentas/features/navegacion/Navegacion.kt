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
//enum class MisCuentasScreen(@StringRes val title: Int){
//    Splash(title = R.string.splash),
//    Login(title = R.string.login),
//    Inicio(title = R.string.inicio),
//    NuevaHoja(title = R.string.nueva_hoja),
//    MisHojas(title = R.string.mis_hojas),
//    NuevoGasto(title = R.string.nuevo_gasto)
//}
sealed class MisCuentasScreen (val route: String) {

    object Splash : MisCuentasScreen("splash")
    object Login : MisCuentasScreen("login",)
    object Inicio : MisCuentasScreen("inicio")
    object NuevaHoja : MisCuentasScreen("nueva_hoja")
    object MisHojas : MisCuentasScreen("mis_hojas")
    object NuevoGasto : MisCuentasScreen("nuevo_gasto")

}

@Composable
fun AppNavHost(
    navController: NavHostController
) {
    val activity = LocalContext.current as FragmentActivity

    //pila de Screen y valor actual
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: MisCuentasScreen.Splash.route

    val canNavigateBack = backStackEntry != null // Determinar si se puede navegar hacia atrás


    NavHost(
        navController = navController,
        startDestination = MisCuentasScreen.Splash.route
    ) {

        composable(MisCuentasScreen.Splash.route) {
            SplashScreen(
                activity,
                onLoginNavigate = { navController.navigate(MisCuentasScreen.Login.route) },
                onInicioNavigate = { navController.navigate(MisCuentasScreen.Inicio.route) }
            )
        }
        composable(MisCuentasScreen.Login.route) {
            Login(
                { navController.navigate(MisCuentasScreen.Inicio.route) }
            )
        }
        composable(MisCuentasScreen.Inicio.route) {
            Inicio(
                currentScreen,
                {navController.navigateUp()},
                onNavSplash = { navController.navigate(MisCuentasScreen.Splash.route) },
                onNavNuevaHoja = { navController.navigate(MisCuentasScreen.NuevaHoja.route) },
                onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.route) }
            )
        }
        composable(MisCuentasScreen.NuevaHoja.route) {
            NuevaHoja(
                currentScreen,
                canNavigateBack,
                {navController.navigateUp()},
                onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.route) }
            )
        }
        composable(MisCuentasScreen.MisHojas.route) {
            MisHojas(
                currentScreen,
                canNavigateBack,
                {navController.navigateUp()},
                navController
            )
        }
        composable(//composable de navegacion el cual recibe un argumentod e tipo Int
            route = MisCuentasScreen.NuevoGasto.route + "/{idHojaPrincipal}",
            arguments = listOf(navArgument(name = "idHojaPrincipal"){
                type = NavType.IntType
            })
        ) {
            // idHojaPrincipal es la clave utilizada para pasar los datos
            NuevoGasto(
                it.arguments?.getInt("idHojaPrincipal"),
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
fun AppNavBar(
    innerPadding: PaddingValues,
    navControllerMisHojas: NavHostController,
    navController: NavHostController
) {
    NavHost(
        navController = navControllerMisHojas,
        startDestination = MisHojasScreen.Gastos.route
    ) {
        composable( MisHojasScreen.Hojas.route ) {
            HojasScreen(
                innerPadding,
                onNavGastos = {//lambda que nos permite pasarle un parametro a la navegacion
                        idHoja ->
                    navControllerMisHojas.navigate(MisHojasScreen.Gastos.route + "/$idHoja")
                }
            )
        }
        composable( MisHojasScreen.Gastos.route
//            route = MisHojasScreen.Gastos.route + "/{idHojaMostrar}",
//            arguments = listOf(navArgument(name = "idHojaMostrar") {
//                type = NavType.IntType
//            })
        ) {
            GastosScreen(
              //  it.arguments?.getInt("idHojaMostrar"),
                innerPadding,
                onNavNuevoGasto = {//lambda que nos permite pasarle un parametro a la navegacion
                        idHoja ->
                    navController.navigate(MisCuentasScreen.NuevoGasto.route + "/$idHoja")
                }
            )
        }
        composable(MisHojasScreen.Participantes.route) {
            ParticipantesScreen()
        }
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
        backgroundColor = MaterialTheme.colorScheme.primary
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

        items.forEach { screen ->
            val isSelected = currentRoute == screen.route //ruta seleccionada para resaltar
            val colorSeleccionado = if (isSelected) MaterialTheme.colorScheme.onBackground else Color.White //resaltar

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
    currentScreen: String,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    val activity = LocalContext.current as FragmentActivity

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


