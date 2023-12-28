package com.app.miscuentas.ui.nueva_hoja.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
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

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun NuevaHoja(
    currentScreen: MisCuentasScreem,
    navController: NavHostController,
    onNavMisHojas: () -> Unit
){

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MiTopBar(
                currentScreen,
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = true,
                navigateUp = { navController.navigateUp() })},
        content = { NuevaHojaContent(onNavMisHojas) }
    )
}


/** CONTENIDO GENERAL DE ESTA SCREEN **/
@Composable
fun NuevaHojaContent(onNavMisHojas: () -> Unit) {

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
                    .height(220.dp) //estira el ancho a 80 dp
                    .clickable { onNavMisHojas() }
            )
        }
    }
}