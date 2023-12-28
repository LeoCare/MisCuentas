package com.app.miscuentas.ui.inicio.ui

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.miscuentas.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** ESTRUCTURA DE VISTA CON SCAFFOLD **/
//Necesario para previsualizar la IU. Con 'name' le cambio el nombre a mostrar.
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Preview(
    showBackground = true,
    name = "Funcion principal"
)
@Composable
fun ScaffoldContent(){
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold( //La funcion Scaffold tiene la estructura para crear una view con barra de navegacion
        scaffoldState = scaffoldState,
        drawerContent = { Text("Menu lateral") },
        topBar = { MiTopBar(scope = scope, scaffoldState = scaffoldState)},
        content = {  ViewContent()}
    )
}


/** BARRA DE NAVEGACION (TOP BAR) **/
@Composable
fun MiTopBar(scope: CoroutineScope, scaffoldState: ScaffoldState){

    val context = LocalContext.current //contexto para la barra de navegacion

    //Cuando showDialog cambia a true se ejecuta MiDialogo(), cuando cambia a false se cierra.
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) MiDialogo({showDialog =  false}, { showDialog = true}) //esta funcion tiene como parametro dos funciones lambda que cambian el valor de una variable.

    TopAppBar(
        title = { Text("Mis Cuentas") },
        navigationIcon = {
            IconButton(
                onClick = {
                    scope.launch { scaffoldState.drawerState.open() }
                }
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Localized description")
            }
        },
        actions = {
            IconButton(
                onClick = {
                    scope.launch {  showDialog = true }
                }
            ) {
                Icon(Icons.Filled.Info, contentDescription = "Informacion")
            }
            IconButton(
                onClick = {
                    scope.launch { Toast.makeText(context, "Mi Toast: Compartir", Toast.LENGTH_LONG).show() }
                }
            ) {
                Icon(Icons.Filled.Share, contentDescription = "Compartir")
            }
        }
    )
}


/** CONTENIDO GENERAL DE LA APP **/
@Composable
fun ViewContent() {

    /** CAJA CON COLUMNAS: **/
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize() //estira al maximo
            .background(Color.LightGray)

    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Alinear horizontalmente en la columna
            verticalArrangement = Arrangement.spacedBy(26.dp), // Espacio entre elementos de la columna
        ) {

            Spacer(modifier = Modifier.padding(40.dp))

            //COMPONENTE IMAGEN
            Image(
                painter = painterResource(id = R.drawable.nueva_hoja),
                contentDescription = "Boton de Nueva_Hoja",
                modifier = Modifier
                    .fillMaxWidth() //estira el largo al maximo
                    .height(180.dp) //estira el ancho a 80 dp
                    .clickable { }
            )

            Image(
                painter = painterResource(id = R.drawable.mis_hojas),
                contentDescription = "Boton de Mis_Hojas",
                modifier = Modifier
                    .fillMaxWidth() //estira el largo al maximo
                    .height(220.dp) //estira el ancho a 80 dp
                    .clickable { }
            )
        }
    }

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

    )

}