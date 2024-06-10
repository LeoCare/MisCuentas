package com.app.miscuentas.features.gastos

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
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
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo
import kotlin.random.Random


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GastosScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Long?,
    onNavNuevoGasto: (Long) -> Unit,
    viewModel: GastosViewModel = hiltViewModel()
) {
    val gastosState by viewModel.gastosState.collectAsState()
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()

    //Hoja a mostrar pasada por el Screen Hojas
    LaunchedEffect(Unit) {
            viewModel.onHojaAMostrar(idHojaAMostrar)
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
        { viewModel.onBorrarGastoChanged(it) },
        gastosState.resumenGastos,
        { viewModel.obtenerParticipantesYSumaGastos() }
    )
}

@Composable
fun GastosContent(
    innerPadding: PaddingValues?,
    hojaDeGastos: HojaConParticipantes?,
    listaIconosGastos: List<IconoGasto>,
    onNavNuevoGasto: (Long) -> Unit,
    onBorrarGastoChanged: (DbGastosEntity?) -> Unit,
    resumenGastos: Map<String, Double>?,
    obtenerParticipantesYSumaGastos: () -> Unit
){
    val isEnabled = hojaDeGastos?.hoja?.status == "C"
    var showResumen by rememberSaveable { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding!!)
            //.background(MaterialTheme.colorScheme.background)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hojaDeGastos?.hoja?.titulo ?: "aun nada" ,
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.Black
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column{
                    Row {
                        Text(
                            text = "Fecha fin: ",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = hojaDeGastos?.hoja?.fechaCierre ?: "no tiene",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Row {
                        Text(
                            text = "Limite: ",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = if(hojaDeGastos?.hoja?.limite.isNullOrEmpty()) "no tiene" else hojaDeGastos?.hoja?.limite.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Row {
                        Text(
                            text = "Participantes: ",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = hojaDeGastos?.participantes?.size.toString(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .clickable {
                        obtenerParticipantesYSumaGastos()
                        showResumen = !showResumen
                    }
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    style = MaterialTheme.typography.titleMedium,
                    text = "Ver Resumen",
                    color = Color.White
                )
                Icon(
                    if(showResumen) Icons.Filled.KeyboardDoubleArrowUp else Icons.Default.KeyboardDoubleArrowDown,
                    contentDescription = "Arrow Icon",
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
            }

            //Expandible para ver el resumen
            AnimatedVisibility(
                visible = showResumen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .padding(16.dp)
                ) {
                    ResumenDesing(resumenGastos, {})
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
            modifier = Modifier.align(Alignment.BottomEnd), // Alinear el botón en la esquina inferior derecha
            isEnabled = isEnabled
            )
    }
}

@Composable
fun GastoDesing(
    gasto: DbGastosEntity?,
    participante: ParticipanteConGastos,
    listaIconosGastos: List<IconoGasto>,
    onBorrarGastoChanged: (DbGastosEntity?) -> Unit
) {
    //Aviso de la opcion elegida:
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo

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
            .padding(horizontal = 7.dp)
            .padding(4.dp)
            .graphicsLayer {
                // Aplica una rotación en el eje Y para crear el efecto 3D
                rotationY = 12f
                // Ajusta la perspectiva para mejorar el efecto 3D
                cameraDistance = 19 * density
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.Black
        )

    ) {
        if (gasto != null){
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 4.dp, start = 1.dp, top = 1.dp, end = 1.dp)
                    .clickable { },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.outline,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding( horizontal = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Column(
                            modifier = Modifier
                                .padding(start = 5.dp, end = 10.dp),
//                            verticalArrangement = Arrangement.spacedBy(1.dp)
                        ) {
                            Image(
                                painter = painterResource(
                                    id = listaIconosGastos[gasto.tipo.toInt() - 1].imagen
                                ), //IMAGEN DEL GASTO
                                contentDescription = "Logo Hoja",
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                            )
                        }
                        Column {
                            Row(
                                modifier = Modifier
                                    .padding(top = 10.dp)
                                    .fillMaxSize(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Text(
                                    text = participante.participante.nombre,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = gasto.importe + "€",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = gasto.concepto,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Row(
                                modifier =  Modifier.fillMaxWidth(),
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
}

@Composable
fun ParticipanteButton(nombre: String, sumaGastos: Double, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(120.dp, 60.dp)
            .background(color, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = nombre, color = Color.White, fontSize = 16.sp, textAlign = TextAlign.Center)
            Text(text = "${sumaGastos}€", color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun ResumenDesing(participantes: Map<String, Double>?, onParticipanteClick: (String) -> Unit) {
    val colors = listOf(Color.Red, Color.Blue, Color.Magenta, Color.Yellow, Color.Green, Color.Black)

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item{
                Column{
                    participantes?.keys?.chunked(2)?.forEach { fila ->
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(fila) { participante ->
                                val colorIndex = (participantes.keys.indexOf(participante) % colors.size)
                                val color = generateRandomColor()//colors[colorIndex]
                                ParticipanteButton(
                                    nombre = participante,
                                    sumaGastos = participantes[participante] ?: 0.0,
                                    color = generateRandomColor(),
                                    onClick = { onParticipanteClick(participante) }
                                )
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
    modifier: Modifier = Modifier,
    isEnabled: Boolean
) {
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo


    if (showDialog) MiAviso(
        show = true,
        texto = "La hoja no esta activa, no puede agregar mas gastos!",
        cerrar = { showDialog = false }
    )

    androidx.compose.material3.FloatingActionButton(
        onClick = {
            if (isEnabled) onNavNuevoGasto()
            else showDialog = true
        },
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

fun generateRandomColor(): Color {
    val random = Random
    val red = random.nextInt(0, 256)
    val green = random.nextInt(0, 256)
    val blue = random.nextInt(0, 256)
    return Color(red, green, blue)
}

//@Preview
//@Composable
//fun Preview(){
//    val innerPadding = PaddingValues()
//    val onNavNuevoGasto: (Int) -> Unit = {}
//    GastosScreen( innerPadding, {onNavNuevoGasto(3)})
//}
