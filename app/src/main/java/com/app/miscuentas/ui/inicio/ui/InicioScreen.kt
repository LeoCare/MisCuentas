package com.app.miscuentas.ui.inicio.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.app.miscuentas.R
import com.app.miscuentas.ui.navegacion.MiTopBar
import com.app.miscuentas.ui.navegacion.MisCuentasScreem

/** ESTRUCTURA DE VISTA CON SCAFFOLD **/
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
//@Preview(
//    showBackground = true,
//    name = "Funcion principal"
//)
@Composable
fun Inicio(
    currentScreen: MisCuentasScreem,
    navController: NavHostController,
    onNavMisHojas: () -> Unit,
    onNavNuevaHoja: () -> Unit
){

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold( //La funcion Scaffold tiene la estructura para crear una view con barra de navegacion
        scaffoldState = scaffoldState,
        drawerContent = { Text("Menu lateral") },
        topBar = {
            MiTopBar(
            currentScreen,
            scope = scope,
            scaffoldState = scaffoldState,
            canNavigateBack = false,
            navigateUp = { navController.navigateUp() })},
        content = { InicioContent(onNavMisHojas, onNavNuevaHoja) }
    )
}



/** CONTENIDO GENERAL DE ESTA SCREEN **/
@Composable
fun InicioContent(onNavMisHojas: () -> Unit, onNavNuevaHoja: () -> Unit) {

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
                    .clickable { onNavNuevaHoja() }
            )

            Image(
                painter = painterResource(id = R.drawable.mis_hojas),
                contentDescription = "Boton de Mis_Hojas",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clickable { onNavMisHojas() }
            )
        }
    }
}