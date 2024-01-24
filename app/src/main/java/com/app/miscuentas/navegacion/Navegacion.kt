@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.miscuentas.navegacion

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.miscuentas.R
import com.app.miscuentas.ui.inicio.ui.Inicio
import com.app.miscuentas.ui.login.ui.Login
import com.app.miscuentas.ui.mis_hojas.ui.MisHojas
import com.app.miscuentas.ui.mis_hojas.ui.nav_bar_screen.GastosScreen
import com.app.miscuentas.ui.mis_hojas.ui.nav_bar_screen.HojasScreen
import com.app.miscuentas.ui.mis_hojas.ui.nav_bar_screen.ParticipantesScreen
import com.app.miscuentas.ui.nueva_hoja.ui.NuevaHoja
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


/** ************************INICIO************************** **/
/** NAVEGACION DEL MAIN ENTRE LAS DISTINTAS SCREEN DE LA APP **/
/** ******************************************************** **/
enum class MisCuentasScreen(@StringRes val title: Int){
    Login(title = R.string.login),
    Inicio(title = R.string.inicio),
    NuevaHoja(title = R.string.nueva_hoja),
    MisHojas(title = R.string.mis_hojas)
}

@Composable
fun AppNavHost(navController: NavHostController) {

    //pila de Screen y valor actual
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreen.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreen.Login.name
    )

    NavHost(
        navController = navController,
        startDestination = MisCuentasScreen.Login.name
    ) {
        composable(MisCuentasScreen.Login.name) {
            Login(
                onNavigate = { navController.navigate(MisCuentasScreen.Inicio.name) }) //Lambda de navegacion a Inicio, para ser usado desde LoginContent()
        }
        composable(MisCuentasScreen.Inicio.name) {
            Inicio(
                currentScreen,
                navController,
                onNavNuevaHoja = { navController.navigate(MisCuentasScreen.NuevaHoja.name) },
                onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.name) }
            )
        }
        composable(MisCuentasScreen.NuevaHoja.name) {
            NuevaHoja(
                currentScreen,
                navController,
                onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.name) }
            )
        }
        composable(MisCuentasScreen.MisHojas.name) {
            MisHojas(
                currentScreen,
                navController
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
        startDestination = MisHojasScreen.Hojas.route
    ) {
        composable(MisHojasScreen.Hojas.route) { HojasScreen(innerPadding, navControllerMisHojas) }
        composable(MisHojasScreen.Gastos.route) { GastosScreen(navControllerMisHojas) }
        composable(MisHojasScreen.Participantes.route) { ParticipantesScreen(navControllerMisHojas) }
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
    currentScreen: MisCuentasScreen,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
){
    val context = LocalContext.current //contexto para la barra de navegacion

    //Cuando showDialog cambia a true se ejecuta MiDialogo(), cuando cambia a false se cierra.
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) MiDialogo("Proximo a gestionar", {showDialog =  false}, { showDialog = true}) //esta funcion tiene como parametro dos funciones lambda que cambian el valor de una variable.

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
                androidx.compose.material3.IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
            else { //si no, muestra el menu lateral
                IconButton(
                    onClick = {
                        scope.launch { scaffoldState.drawerState.open() }
                    }
                ) {
                    Icon(Icons.Filled.Menu, contentDescription = "Localized description")
                }
            }
        },
        actions = {
            IconButton(
                onClick = {
                    scope.launch { Toast.makeText(context, "Mi Toast: Compartir", Toast.LENGTH_LONG).show() }
                }
            ) {
                Icon(Icons.Filled.Share, contentDescription = "Compartir")
            }
            IconButton(
                onClick = {
                    scope.launch {  showDialog = true }
                }
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Informacion")
            }
        }

    )
}


/** DIALOGO **/
//Esta funcion recibe el resultado de cada una de las dos funciones de sus parametros, para ser usados dentro de ella.
//Al recibir valores lambda, se entiende que el resultado de esas funciones lambda tienen sentido desde donde se llame a MiDialogo().
//Es decir, dependiendo lo que pase en AlertDialog() ejecutara una lambda u otra (cerrar() o aceptar() )
@Composable
fun MiDialogo(texto: String, cerrar: () -> Unit, aceptar: () -> Unit) {

    AlertDialog(onDismissRequest = { cerrar() },
        confirmButton = {
            TextButton(onClick = { aceptar() }) {
                Text(text = "Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = { cerrar() }) {
                Text(text = "Cerrar")
            }
        },
        title = { Text(text = "Mi Diaologo") },
        text = { Text(text = texto) }
    )
}