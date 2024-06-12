package com.app.miscuentas.features.gastos

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.KeyboardDoubleArrowUp
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
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
import com.app.miscuentas.util.Desing
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo
import com.app.miscuentas.util.Desing.Companion.MiDialogoWithOptions
import kotlinx.coroutines.Delay
import okhttp3.internal.concurrent.Task
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
    val colors = listOf(
        generateRandomColor(),
        generateRandomColor(),
        generateRandomColor(),
        generateRandomColor(),
        generateRandomColor(),
        generateRandomColor()
    )

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
        texto = "Tratar un aviso si los gastos no han sido guardados en BBDD, antes de salir atras.",
        { showDialog = false }
    )


    GastosContent(
        innerPadding,
        gastosState.hojaAMostrar,
        listaIconosGastos,
        { onNavNuevoGasto(it) },
        { viewModel.onBorrarGastoChanged(it) },
        gastosState.existeRegistrado,
        gastosState.resumenGastos,
        gastosState.balanceDeuda,
        { viewModel.obtenerParticipantesYSumaGastos() },
        { viewModel.calcularDeudas() },
        colors
    )
}

@Composable
fun GastosContent(
    innerPadding: PaddingValues?,
    hojaDeGastos: HojaConParticipantes?,
    listaIconosGastos: List<IconoGasto>,
    onNavNuevoGasto: (Long) -> Unit,
    onBorrarGastoChanged: (DbGastosEntity?) -> Unit,
    existeRegistrado: Boolean,
    resumenGastos: Map<String, Double>?,
    balanceDeuda: Map<String, Double>?,
    obtenerParticipantesYSumaGastos: () -> Unit,
    calcularDeudas: () -> Unit,
    colors: List<Color>
){
    val isEnabled = hojaDeGastos?.hoja?.status == "C"
    var showResumen by rememberSaveable { mutableStateOf(false) }
    var showBalance by rememberSaveable { mutableStateOf(false) }


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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Row(
                    Modifier
                        .clickable {
                            obtenerParticipantesYSumaGastos()
                            showResumen = !showResumen
                        },
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        Icons.Default.AccountBox,
                        contentDescription = "Arrow Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Resumen",
                        color = Color.White
                    )
                }

                Row(
                    Modifier
                        .clickable {
                            calcularDeudas()
                            showBalance = !showBalance
                        },
                    verticalAlignment = Alignment.CenterVertically
                    ){
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = "Arrow Icon",
                        tint = Color.Black,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Balance",
                        color = Color.White
                    )
                }
            }

            //Expandible para ver el resumen:
            AnimatedVisibility(
                visible = showResumen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    ResumenDesing(
                        participantes = resumenGastos,
                        onParticipanteClick = {},
                        colors = colors)
               }
            }

            //Expandible para ver el balance:
            AnimatedVisibility(
                visible = showBalance,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(2.dp)
                ) {
                    Balance(
                        exiteRegistrado = existeRegistrado,
                        participantes = balanceDeuda,
                        tomarFotoGasto = {}
                    )
                }
            }

            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxSize()
            ) {
                if (hojaDeGastos != null) {
                    itemsIndexed(hojaDeGastos.participantes) { index, participante ->
                        if (participante.gastos.isNotEmpty()){
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
                }
            }
        }
        CustomFloatButton(
            onNavNuevoGasto = { onNavNuevoGasto(hojaDeGastos?.hoja?.idHoja!!) },
            modifier = Modifier.align(Alignment.BottomEnd), // Alinear el botón en la esquina inferior derecha
            isEnabled = isEnabled,
            showBalance = {
                showBalance = false
                showResumen = false
            }
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
        titulo = "ELIMINAR GASTO",
        mensaje = "Si acepta, se eliminara y no se podrá recuperar.",
        cerrar = { showDialog = false },
        aceptar = {
            onBorrarGastoChanged(gasto)
            showDialog = false
        }
    )
    Surface(
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        modifier = Modifier
            .padding(vertical = 3.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = listaIconosGastos[gasto!!.tipo.toInt() - 1].imagen),
                contentDescription = "Icono del gasto",
                modifier = Modifier
                    .size(55.dp)
                    .background(Color.Gray, shape = CircleShape)
                    .padding(1.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
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
                Text(text = gasto.concepto, style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier =  Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pagado el ${gasto.fechaGasto}",
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

/** RESUMEN **/
@Composable
fun ParticipanteButton(nombre: String, sumaGastos: Double, color: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(2.dp)
            .size(100.dp, 60.dp)
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
fun ResumenDesing(
    participantes: Map<String, Double>?,
    onParticipanteClick: (String) -> Unit,
    colors: List<Color>
) {
    LazyColumn(
        contentPadding = PaddingValues(1.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        item{
            Column{
                participantes?.keys?.chunked(3)?.forEach { fila ->
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(fila) { participante ->
                            val colorIndex = (participantes.keys.indexOf(participante) % colors.size)
                            val color = colors[colorIndex]
                            ParticipanteButton(
                                nombre = participante,
                                sumaGastos = participantes[participante] ?: 0.0,
                                color = color,
                                onClick = { onParticipanteClick(participante) }
                            )
                        }
                    }
                }
            }
        }
    }
}
/*****************************************/


/** BALANCE **/
@Composable
fun BalanceDesing(
    posicion: Int,
    exiteRegistrado: Boolean,
    participantes: Map<String, Double>?,
    participante: String,
    monto: Double,
    tomarFotoGasto: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    var titulo by rememberSaveable { mutableStateOf("") } //Titulo a mostrar
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar
    var opcionSeleccionada by rememberSaveable { mutableStateOf("") }
    var opcionAceptada by rememberSaveable { mutableStateOf(false) }

    //actualiza el state que se usara en el composable principal de esta screen
    if(opcionAceptada) {
        Toast.makeText(context, "Enviado mensaje de pago a $opcionSeleccionada", Toast.LENGTH_SHORT).show()
        opcionAceptada = false
    }

    if (showDialog) participantes?.let { listaParticipantes ->
        val listaParticipantesSinPrimero = listaParticipantes.toList().drop(1).toMap()
        MiDialogoWithOptions(
            show = true,
            participantes = listaParticipantesSinPrimero,
            titulo = titulo,
            mensaje = mensaje,
            cancelar = { showDialog = false },
            aceptar = {
                //onBorrarGastoChanged(gasto)
                opcionAceptada = true
                showDialog = false
            },
            onParticipantSelected = { opcionSeleccionada = it }
        )
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outline,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ){
                Text(
                    text = participante,
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp
                )
            Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if(monto > 0) "Recibe" else if(monto < 0) "Debe" else "Saldado",
                    fontSize = 14.sp,
                    color = if(monto > 0) MaterialTheme.colorScheme.onSecondaryContainer else if(monto < 0) Color.Red else Color.Black
                )
            Spacer(modifier = Modifier.width(14.dp))
                Text(
                    text = String.format("%.2f €", monto),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if(exiteRegistrado){
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(posicion == 0){
                        Button(
                            enabled = monto != 0.0,
                            onClick = {
                                if(monto > 0) Toast.makeText(context, "Se ha solicitado el pago a los deudores", Toast.LENGTH_SHORT).show()
                                else {
                                    titulo = "PAGAR A.."
                                    mensaje = "Enviar mensaje de pago."
                                    showDialog = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                        ) {
                            Text(
                                text = if(monto > 0) "SOLICITAR" else if(monto < 0) "PAGAR A.." else "LISTO",
                                fontSize = 14.sp
                            )
                        }
                        if(monto < 0) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Comprobante del pago",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                IconButton(onClick = tomarFotoGasto) {
                                    Icon(
                                        Icons.Default.PhotoCamera,
                                        contentDescription = "Tomar foto",
                                        tint = MaterialTheme.colorScheme.primary
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
fun Balance(
    exiteRegistrado: Boolean,
    participantes: Map<String, Double>?,
    tomarFotoGasto: () -> Unit
) {
    Column {
        if(!exiteRegistrado) Text(
            modifier = Modifier.padding(horizontal = 7.dp),
            text = "Tu no estas en esta hoja de gastos. No puedes reclamar ni abonar."
        )
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            participantes?.let {
                itemsIndexed(participantes.toList()) { _index, (nombre, monto) ->
                    BalanceDesing(
                        posicion = _index,
                        exiteRegistrado = exiteRegistrado,
                        participantes = participantes,
                        participante = nombre,
                        monto = monto,
                        tomarFotoGasto = { }
                    )
                }
            }
        }
    }

}

/**********************************************/

@Composable
fun CustomFloatButton(
    onNavNuevoGasto: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean,
    showBalance: () -> Unit
) {
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo


    if (showDialog) MiAviso(
        show = true,
        texto = "La hoja no esta activa, no puede agregar mas gastos!",
        cerrar = { showDialog = false }
    )

    androidx.compose.material3.FloatingActionButton(
        onClick = {
            showBalance()
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
