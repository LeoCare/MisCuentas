package com.app.miscuentas.features.gastos

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GastosScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Long?,
    onNavNuevoGasto: (Long) -> Unit,
    viewModel: GastosViewModel = hiltViewModel()
) {
    val gastosState by viewModel.gastosState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()

    //Hoja a mostrar pasada por el Screen Hojas (si es 0 es por defecto pasada por el NavBar)
    LaunchedEffect(Unit) {
        if (idHojaAMostrar?.toInt() != 0)
            viewModel.onHojaAMostrar(idHojaAMostrar)
        else
            viewModel.getHojaCalculoPrincipal()
    }

    //Borrar un gasto
    LaunchedEffect(gastosState.gastoElegido){
        if(gastosState.gastoElegido != null){
            viewModel.deleteGasto()
        }
    }

    //Este aviso se lanzara cuando se deniega el permiso...
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) MiAviso(
        show = true,
        texto = "Tratar un aviso si los gastos no han sido guardados en BBDD, antes de salir atras."
    )
    { showDialog = false }

    GastosContent(
        innerPadding,
        gastosState.hojaAMostrar,
        listaIconosGastos,
        { onNavNuevoGasto(it) },
        { viewModel.onBorrarGastoChanged(it) }
    )
}

@Composable
fun GastosContent(
    innerPadding: PaddingValues?,
    hojaDeGastos: HojaConParticipantes?,
    listaIconosGastos: List<IconoGasto>,
    onNavNuevoGasto: (Long) -> Unit,
    onBorrarGastoChanged: (DbGastosEntity?) -> Unit
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
                    text = hojaDeGastos?.hoja?.titulo ?: "aun nada" ,
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
                        text = hojaDeGastos?.hoja?.fechaCierre ?: "no tiene",
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
                        text = if(hojaDeGastos?.hoja?.limite == null) "no tiene" else hojaDeGastos.hoja.limite.toString(),
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
                    itemsIndexed(hojaDeGastos.participantes) { index, participante ->
                        for (gasto in participante.gastos) {
                            GastoDesing(
                                idHoja = hojaDeGastos.hoja.idHoja,
                                gasto = gasto,
                                participante = participante,
                                listaIconosGastos,
                                { onBorrarGastoChanged(it) }
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
            onNavNuevoGasto = { onNavNuevoGasto(hojaDeGastos?.hoja?.idHoja!!) },
            modifier = Modifier.align(Alignment.BottomEnd) // Alinear el botón en la esquina inferior derecha
        )
    }
}

@Composable
fun GastoDesing(
    idHoja: Long,
    gasto: DbGastosEntity?,
    participante: ParticipanteConGastos,
    listaIconosGastos: List<IconoGasto>,
    onBorrarGastoChanged: (DbGastosEntity?) -> Unit
) {
    //Aviso de la opcion elegida:
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    val gastoABorrar = arrayOf(idHoja, participante.participante.idParticipante, gasto!!.idGasto)

    if (showDialog) MiDialogo(
        show = true,
        texto = "¿Borrar este gasto?",
        cerrar = { showDialog = false },
        aceptar = {
            onBorrarGastoChanged(gasto)
            showDialog = false
        }
    )
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
                                id = listaIconosGastos[gasto.idGasto.toInt() - 1].imagen
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
                                text = participante.participante.nombre,
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
                                text = "Pagado el " + gasto.fechaGasto.toString(),
                                style = MaterialTheme.typography.labelLarge
                            )
                            IconButton(onClick = { showDialog = true }) {
                                Icon(
                                    Icons.Default.DeleteForever,
                                    contentDescription = "Borrar gasto",
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