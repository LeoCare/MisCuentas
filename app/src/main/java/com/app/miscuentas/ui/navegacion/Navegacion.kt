package com.app.miscuentas.ui.navegacion

import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.app.miscuentas.R
import com.app.miscuentas.ui.inicio.ui.Inicio
import com.app.miscuentas.ui.login.ui.Login
import com.app.miscuentas.ui.nueva_hoja.ui.NuevaHoja
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

enum class MisCuentasScreem(@StringRes val title: Int){
    Login(title = R.string.login),
    Inicio(title = R.string.inicio),
    Nueva_Hoja(title = R.string.nueva_hoja),
    Mis_Hojas(title = R.string.mis_hojas)
}

@Composable
fun AppNavHost(navController: NavHostController) {

    //pila de Screen y valor actual
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreem.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreem.Login.name
    )

    NavHost(
        navController = navController,
        startDestination = MisCuentasScreem.Login.name
    ) {
        composable(MisCuentasScreem.Login.name) {
            Login(
                onNavigate = { navController.navigate(MisCuentasScreem.Inicio.name) }) //Lambda de navegacion a Inicio, para ser usado desde LoginContent()
        }
        composable(MisCuentasScreem.Inicio.name) {
            Inicio(
                currentScreen,
                navController,
                onNavNuevaHoja = { navController.navigate(MisCuentasScreem.Nueva_Hoja.name) },
                onNavMisHojas = { navController.navigate(MisCuentasScreem.Mis_Hojas.name) }
            )
        }
        composable(MisCuentasScreem.Nueva_Hoja.name) {
            NuevaHoja(
                currentScreen,
                navController,
                onNavMisHojas = { navController.navigate(MisCuentasScreem.Mis_Hojas.name) }
            )
        }
        composable(MisCuentasScreem.Mis_Hojas.name) {

        }
    }
}


@Composable
fun MiTopBar(
    currentScreen: MisCuentasScreem,
    scope: CoroutineScope,
    scaffoldState: ScaffoldState,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
){
    val context = LocalContext.current //contexto para la barra de navegacion

    //Cuando showDialog cambia a true se ejecuta MiDialogo(), cuando cambia a false se cierra.
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) MiDialogo({showDialog =  false}, { showDialog = true}) //esta funcion tiene como parametro dos funciones lambda que cambian el valor de una variable.

    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
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
        },
        backgroundColor = Color(color = 0xFFA397E6)
    )
}


/** DIALOGO **/
//Esta funcion recibe el resultado de cada una de las dos funciones de sus parametros, para ser usados dentro de ella.
//Al recibir valores lambda, se entiende que el resultado de esas funciones lambda tienen sentido desde donde se llame a MiDialogo().
//Es decir, dependiendo lo que pase en AlertDialog() ejecutara una lambda u otra (cerrar() o aceptar() )
@Composable
fun MiDialogo(cerrar: () -> Unit, aceptar: () -> Unit) {

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
        text = { Text(text ="Contenido del mensaje a mostrar") }
    )
}