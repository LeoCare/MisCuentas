package com.app.miscuentas.features.balances

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo
import com.app.miscuentas.util.Desing.Companion.MiDialogoWithOptions
import java.text.NumberFormat
import kotlin.math.abs


@Composable
fun BalanceScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Long?,
    viewModel: BalanceViewModel = hiltViewModel()
){
    val balancesState by viewModel.balanceState.collectAsState()
    val context = LocalContext.current

    BalanceContent(
        innerPadding = innerPadding,
        hojaDeGastos = balancesState.hojaDeGastos,
        participantes = balancesState.balanceDeuda,
        existeRegistrado = balancesState.existeRegistrado,
    )
}


@Composable
fun BalanceContent(
    innerPadding: PaddingValues?,
    hojaDeGastos: HojaConParticipantes?,
    participantes: Map<String, Double>?,
    existeRegistrado: Boolean
){
    val montoRegistrado = participantes!!.firstNotNullOf { it.value } //mi monto


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding!!)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Surface(
                shape = RoundedCornerShape(1.dp),
                elevation = 2.dp,
                modifier = Modifier
                    .padding(vertical = 5.dp, horizontal = 5.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.spacedBy(180.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = hojaDeGastos?.hoja?.titulo ?: "aun nada",
                                style = MaterialTheme.typography.titleLarge,
                                fontSize = 24.sp
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                DatosHoja(hojaDeGastos)

                                LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                                    if (!participantes.isNullOrEmpty()) {
                                        /** LISTA CON LOS PARTICIPANTES Y SU BALANCE **/
                                        item {
                                            Text(text = "Deuda:")
                                            LazyRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceEvenly,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                items(
                                                    participantes.toList(), key = { it.first }) { (nombre, monto) ->
                                                    BalanceDesing(
                                                        participante = nombre,
                                                        monto = monto,
                                                        paddVert = 10
                                                    )
                                                }
                                            }
                                        }

                                        /** RECUADRO CON ACCIONES DE RESOLUCION **/
                                        item {
                                            ResolucionBox(
                                                existeRegistrado,

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
    }
}


@Composable
fun DatosHoja(hojaDeGastos: HojaConParticipantes?){
    Row {
        Text(
            text =  when (hojaDeGastos?.hoja?.status) {
                "C" -> "Activa"
                "A" -> "Anulada"
                "B" -> "Balanceada"
                else ->"Finalizada"
            },
            fontSize = 20.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleSmall
        )
    }
    Row {

        Text(
            text = "Fecha Cierre: ",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = hojaDeGastos?.hoja?.fechaCierre ?: "-",
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Row {
        Text(
            text = "Limite: ",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = if(hojaDeGastos?.hoja?.limite.isNullOrEmpty()) "-" else hojaDeGastos?.hoja?.limite.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = "Participantes: ",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = hojaDeGastos?.participantes?.size.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun BalanceDesing(
    participante: String,
    monto: Double,
    paddVert: Int
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance()

    Surface(
        shape = RoundedCornerShape(18.dp),
        elevation = 6.dp,
        modifier = Modifier
            .padding(vertical = paddVert.dp, horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(28.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = participante,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (monto > 0) "Recibe" else if (monto < 0) "Debe" else "Saldado",
                fontSize = 14.sp,
                color = if (monto > 0) MaterialTheme.colorScheme.onSecondaryContainer else if (monto < 0) Color.Red else Color.Black
            )
            Text(
                text = currencyFormatter.format(monto),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ResolucionBox(
    exiteRegistrado: Boolean,
    participantes: Map<String, Double>?,
    pagoRealizado: Boolean,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    pagarDeuda: (Pair<String, Double>?) -> Unit,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit,
    imagenUri: Uri?
){
    val montoRegistrado = participantes!!.firstNotNullOf { it.value } //mi monto
    val context = LocalContext.current
    var titulo by rememberSaveable { mutableStateOf("") } //Titulo a mostrar
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar
    var opcionSeleccionada by rememberSaveable { mutableStateOf<Pair<String, Double>?>(null) }
    val currencyFormatter = NumberFormat.getCurrencyInstance()

    LaunchedEffect(pagoRealizado) {
        if (pagoRealizado){
            opcionSeleccionada = null
            onPagoRealizadoChanged(false)
        }
    }

    //AVISO PARA MOSTRAR LOS ACREEDORES A PAGAR:
    var showDialogWhitOptions by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialogWhitOptions) participantes.let { listaParticipantes ->
        val listaParticipantesSinPrimero = listaParticipantes
            .filter { it.value > 0.0 } // Filtrar valores mayores a 0.0
            .toList() // Convertir a lista
            .toMap() // Convertir de nuevo a mapa
        MiDialogoWithOptions(
            show = true,
            participantes = listaParticipantesSinPrimero,
            titulo = titulo,
            mensaje = mensaje,
            cancelar = { showDialogWhitOptions = false },
            onParticipantSelected = {
                opcionSeleccionada = it
                showDialogWhitOptions = false
            }
        )
    }

    //AVISO CUANDO SE PAGA LA DEUDA:
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    if (showConfirm){
        MiDialogo(show = true,
            titulo = titulo ,
            mensaje = mensaje ,
            cerrar = { showConfirm = false },
            aceptar = {
                pagarDeuda(opcionSeleccionada)
                showConfirm = false
                Toast.makeText(context, "Enviado mensaje de pago a ${opcionSeleccionada?.first}", Toast.LENGTH_SHORT).show()
            })
    }

    //AVISO CUANDO SE DENIEGA EL PERMISO:
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) {
        MiAviso(
            show = true,
            texto = mensaje,
            cerrar = {
                showDialog = false

            }
        )
    }

    if(exiteRegistrado && montoRegistrado == 0.0) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outline,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(7.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Nada que resolver")
            }
        }
    }
}
else {
    Spacer(modifier = Modifier.size(20.dp))
    Text(text = "Resolver:")
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outline,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(7.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                /** OPCION 1: ELEGIR ACREEDOR **/
                /** OPCION 1: ELEGIR ACREEDOR **/
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "1 - ")
                    Text(
                        text = if (montoRegistrado > 0) "Solicitar el pago.." else if (montoRegistrado < 0) "Pagar a..." else "Saldado",
                        fontWeight = FontWeight.Black,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (opcionSeleccionada != null) {
                    Text(
                        text = opcionSeleccionada!!.first,
                        fontSize = 25.sp,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .clickable {
                                if (montoRegistrado > 0) Toast.makeText(
                                    context,
                                    "Se ha solicitado el pago a los deudores",
                                    Toast.LENGTH_SHORT
                                ).show()
                                else {
                                    titulo = "PAGAR A.."
                                    mensaje = "(Solo se descontará tu parte de la deuda)"
                                    showDialogWhitOptions = true
                                }
                            },
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Icon(
                        Icons.Filled.People,
                        "Elegir Participante",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                if (montoRegistrado > 0) Toast
                                    .makeText(
                                        context,
                                        "Se ha solicitado el pago a los deudores",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                                else {
                                    titulo = "PAGAR A.."
                                    mensaje =
                                        "(Solo se descontará tu parte de la deuda)"
                                    showDialogWhitOptions = true
                                }
                            },
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                if (opcionSeleccionada?.second != null) {
                    Text(
                        text = if (opcionSeleccionada!!.second > abs(montoRegistrado)) currencyFormatter.format(
                            abs(montoRegistrado)
                        ) else currencyFormatter.format(opcionSeleccionada!!.second),
                        fontSize = 14.sp
                    )
                } else Spacer(Modifier.width(30.dp))
            }
            /** OPCION 2: COMPROBANTE **/
            /** OPCION 2: COMPROBANTE **/
            if (montoRegistrado < 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row {
                        Text(text = "2 - ")
                        Text(
                            text = "Adjuntar comprobante..",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Row {
                        IconButton(onClick = { tomarFoto() }) {
                            Icon(
                                Icons.Default.PhotoCamera,
                                contentDescription = "Tomar foto",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = { elegirImagen() }) {
                            Icon(
                                Icons.Default.PhotoLibrary,
                                contentDescription = "Galeria",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    AsyncImage(
                        model = imagenUri,
                        contentDescription = "Foto del comprobante de pago",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .padding(top = 16.dp)
                    )
                }
                /** OPCION 3: ENVIAR **/
                /** OPCION 3: ENVIAR **/
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row {
                        Text(text = "3 - ")
                        Text(
                            text = "Enviar",
                            fontWeight = FontWeight.Black,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Icon(
                        Icons.Filled.Email,
                        "Enviar Aviso",
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {
                                if (opcionSeleccionada != null) {
                                    titulo = "CONFIRMAR"
                                    mensaje =
                                        "Si acepta se enviará un mensaje a ${opcionSeleccionada?.first} por el pago de ${
                                            if (opcionSeleccionada!!.second > abs(
                                                    montoRegistrado
                                                )
                                            ) currencyFormatter.format(
                                                abs(
                                                    montoRegistrado
                                                )
                                            ) else currencyFormatter.format(
                                                opcionSeleccionada!!.second
                                            )
                                        }"
                                    showConfirm = true
                                }
                            },
                        tint = if (opcionSeleccionada != null) MaterialTheme.colorScheme.primary else Color.LightGray
                    )
                    Spacer(Modifier.width(10.dp))
                }
            }
        }
    }
}