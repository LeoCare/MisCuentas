package com.app.miscuentas.ui.mis_hojas.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Difference
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
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
        backStackEntry?.destination?.route ?: MisCuentasScreem.MisHojas.name
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
        backgroundColor = MaterialTheme.colorScheme.primary

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
    // Screen con las hojas creadas
    //Provisional!!!
    val itemsTipo = listOf("Activas", "Finalizadas", "Todas")
    val itemsOrden = listOf("Fecha creacion", "Gasto total")


    Box{
        LazyColumn {
            item {

                Row(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    SpinnerCustoms(itemsTipo, "Filtrar por tipo")
                    //CustomSpacer(10.dp)
                    SpinnerCustoms(itemsOrden, "Opcion de ordenacion")

                }
            }
        }
    }

}

@Composable
fun GastosScreen(navController: NavController) {
    // Screen con la lista de los gastos de la hoja seleccionada
}

@Composable
fun ParticipantesScreen(navController: NavController) {
    // Participantes y estadisticas de la hoja seleccionada
}


//Metodo para los menus de opciones de filtrado
@Composable
fun SpinnerCustoms(items: List<String>, contentDescription: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Card(
        modifier = Modifier
            .padding(20.dp)
            .height(IntrinsicSize.Min)
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.Black
        )
    ) {

        Row(modifier = Modifier.padding(6.dp))
        {
            Text(text = AnnotatedString(items[selectedIndex]))
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = contentDescription,
                tint = MaterialTheme.colorScheme.primary
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEachIndexed { index, item ->

                    DropdownMenuItem(onClick = {
                        selectedIndex = index
                        expanded = false
                    }) {
                        Text(text = item)
                    }
                }
            }

        }
    }
}

//ESPACIADOR
@Composable
fun CustomSpacer(size: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
    )
}