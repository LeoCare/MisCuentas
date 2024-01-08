package com.app.miscuentas.ui.inicio.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.R
import com.app.miscuentas.ui.MiTopBar
import com.app.miscuentas.ui.MisCuentasScreem

//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
@Preview
@Composable
fun Prev(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreem.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreem.Inicio.name
    )

    Inicio(
        currentScreen,
        navController,
        onNavNuevaHoja = { navController.navigate(MisCuentasScreem.Nueva_Hoja.name) },
        onNavMisHojas = { navController.navigate(MisCuentasScreem.Mis_Hojas.name) }
    )
}

/** ESTRUCTURA DE VISTA CON SCAFFOLD **/
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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
            navigateUp = { navController.navigateUp() })
        },
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
            .background(Color(color = 0xFFF5EFEF))

    ) {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally, // Alinear horizontalmente en la columna
            verticalArrangement = Arrangement.spacedBy(26.dp), // Espacio entre elementos de la columna
        ) {
            val robotoItalic = FontFamily(Font(R.font.roboto_bolditalic))

            item {
                CustomSpacer(40.dp)

                Card(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .height(IntrinsicSize.Min)
                        .clickable { onNavMisHojas() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .size(330.dp, 150.dp)
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ver...",
                            modifier = Modifier
                                .align(Alignment.Bottom),
                            fontSize = 30.sp,
                            fontFamily = robotoItalic
                        )
                        ImagenCustom(R.drawable.mis_hojas, "Boton de Mis_Hojas")
                    }
                }
                CustomSpacer (30.dp)
                Card(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .height(IntrinsicSize.Min)
                        .clickable { onNavNuevaHoja() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.outlineVariant)
                            .size(330.dp, 150.dp)
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Crear...",
                            modifier = Modifier
                                .align(Alignment.Bottom),
                            fontSize = 30.sp,
                            fontFamily = robotoItalic
                        )
                        ImagenCustom(R.drawable.nueva_hoja, "Boton de Nueva_Hoja")
                    }
                }
            }
        }
    }
}

//ESPACIADOR
@Composable
fun CustomSpacer(padding: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
    )
}


//COMPONENTE IMAGEN
@Composable
fun ImagenCustom(
    imagen: Int,
    descript: String
){
    Image(
        painter = painterResource(id = imagen),
        contentDescription = descript,
        modifier = Modifier
            .width(180.dp)
            .height(180.dp)
    )
}
