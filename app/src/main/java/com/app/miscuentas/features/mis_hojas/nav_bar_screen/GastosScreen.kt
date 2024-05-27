package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.Gasto
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GastosScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Int?,
    onNavNuevoGasto: (Int) -> Unit,
    viewModel: GastosViewModel = hiltViewModel()
) {
    val gastosState by viewModel.gastosState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()

    //Hoja a mostrar pasada por el Screen Hojas (si es 0 es por defecto pasada por el NavBar)
    LaunchedEffect(Unit) {
        if (idHojaAMostrar != 0)
            viewModel.onHojaAMostrar(idHojaAMostrar)
        else
            viewModel.getHojaCalculoPrincipal()
    }

    LaunchedEffect(gastosState.opcionSelected){
        when(gastosState.opcionSelected) {
          //  "Resumen" ->  { viewModel.update(gastosState.hojaAMostrar!!) }
            "Finalizar" -> { viewModel.update(gastosState.hojaAMostrar!!) }
            "Anular" -> { viewModel.update(gastosState.hojaAMostrar!!) }
        }
    }

    //Este aviso se lanzara cuando se deniega el permiso...
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) MiAviso(
        show = true,
        texto = "Tratar un aviso si los gastos no han sido guardados en BBDD, antes de salir atras."
    )
    { showDialog = false }
    Scaffold(
        scaffoldState = scaffoldState,
        content = {
            GastosContent(
                innerPadding,
                gastosState.hojaAMostrar,
                gastosState.opcionSelected,
                listaIconosGastos,
                { onNavNuevoGasto(it) },
                { viewModel.onOpcionSelectedChanged(it) },
                { viewModel.onStatusChanged(it) }
            )
        }
    )
}

@Composable
fun GastosContent(
    innerPadding: PaddingValues?,
    hojaDeGastos: HojaCalculo?,
    opcionSelected: String,
    listaIconosGastos: List<IconoGasto>,
    onNavNuevoGasto: (Int) -> Unit,
    onOpcionSelectedChanged: (String) -> Unit,
    onStatusChanged: (String) -> Unit,
){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding!!)
            .background(MaterialTheme.colorScheme.background)
    ) {
        //Manejar la vuelta atras del usuario
//        BackHandler {
//            if (!gastosState.datosGuardados) {
//                showDialog = true
//                viewModel.setDatosGuardados(false)
//            }
//        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hojaDeGastos?.titulo ?: "aun nada" ,
                    style = MaterialTheme.typography.displayMedium,
                    color = Color.Black
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row {
                    Text(
                        text = "Fecha fin: ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = hojaDeGastos?.fechaCierre ?: "no tiene",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Row {
                    Text(
                        text = "Limite: ",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = if(hojaDeGastos?.limite == null) "no tiene" else hojaDeGastos.limite!!.toString(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

            }

            LazyColumn(
                modifier = Modifier
                    .padding(top = 40.dp)

            ) {
                if (hojaDeGastos != null) {
                    itemsIndexed(hojaDeGastos.participantesHoja) { index, participante ->
                        for (gasto in participante!!.listaGastos) {
                            GastoDesing(
                                gasto = gasto,
                                participante = participante,
                                listaIconosGastos,
                                {onOpcionSelectedChanged(it)}
                            )
                        }
                    }
                }

                /*Prev -> LazyColumn{
               items(gastosState.listaGastos){gasto ->
                   GastoDesing(gasto = gasto)

               }
           }*/

            }
        }
        CustomFloatButton(
            onNavNuevoGasto = { onNavNuevoGasto(hojaDeGastos!!.id) },
            modifier = Modifier.align(Alignment.BottomEnd) // Alinear el botón en la esquina inferior derecha
        )
    }
}

@Composable
fun GastoDesing(
    gasto: Gasto?,
    participante: Participante,
    listaIconosGastos: List<IconoGasto>,
    onOpcionSelectedChanged: (String) -> Unit

) {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    //Aviso de la opcion elegida:
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar
    var opcionSeleccionada by rememberSaveable { mutableStateOf("") }

    if (showDialog) MiDialogo(
        show = true,
        texto = mensaje,
        cerrar = { showDialog = false },
        aceptar = {
            onOpcionSelectedChanged(opcionSeleccionada) //cambiar a funcion de update gasto por A de anulado!!
            showDialog = false
        }
    )

    if (gasto != null) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 30.dp, bottom = 15.dp)
                .padding(4.dp)
                .graphicsLayer {
                    // Aplica una rotación en el eje Y para crear el efecto 3D
                    rotationY = 22f
                    // Ajusta la perspectiva para mejorar el efecto 3D
                    cameraDistance = 13 * density
                },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = Color.Black
            )

        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp, start = 6.dp, top = 1.dp, end = 1.dp)
                    .clickable { },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.outline,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp, horizontal = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Column(
                            modifier = Modifier
                                .padding(start = 5.dp, end = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Image(
                                painter = painterResource(
                                    id = listaIconosGastos[gasto.id_gasto.toInt() - 1].imagen
                                ), //IMAGEN DEL GASTO
                                contentDescription = "Logo Hoja",
                                modifier = Modifier
                                    .width(60.dp)
                                    .height(60.dp)
                            )
                        }
                        Column {
                            //Text(text = hoja.type)
                            Row(
                                modifier = Modifier
                                    .padding(bottom = 10.dp)
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Text(
                                    text = participante.nombre,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text(
                                    text = gasto.importe + "€",
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 10.dp),
                                text = gasto.concepto,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Row(
                                modifier =  Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Pagado el " + gasto.fecha_gasto.toString(),
                                    style = MaterialTheme.typography.labelLarge
                                )
                                IconButton(onClick = { showDialog = true }) {
                                    Icon(
                                        Icons.Default.DeleteForever,
                                        contentDescription = "Menu de opciones",
                                        tint = Color.Red
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CustomFloatButton(
    onNavNuevoGasto: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = { onNavNuevoGasto() },
        modifier = modifier
            .height(80.dp)
            .width(80.dp)
            .padding(bottom = 14.dp, end = 14.dp), // Añade el padding al botón flotante
        shape = MaterialTheme.shapes.large
    ) {
        Image(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inversePrimary),
            painter = painterResource(id = R.drawable.nuevo_gasto), //IMAGEN DEL GASTO
            contentDescription = "Logo Hoja",
        )
    }
}

//@Preview
//@Composable
//fun Preview(){
//    val innerPadding = PaddingValues()
//    val onNavNuevoGasto: (Int) -> Unit = {}
//    GastosScreen( innerPadding, {onNavNuevoGasto(3)})
//}
