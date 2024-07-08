package com.app.miscuentas.features.balance

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.miscuentas.R
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo
import com.app.miscuentas.util.Desing.Companion.MiDialogoWithOptions
import com.app.miscuentas.util.Desing.Companion.MiImagenDialog
import com.app.miscuentas.util.Imagen.Companion.permisosAlmacenamiento
import java.text.NumberFormat
import kotlin.math.abs


@Composable
fun BalanceScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Long?,
    viewModel: BalanceViewModel = hiltViewModel()
){
    val balanceState by viewModel.balanceState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.onHojaAMostrar(idHojaAMostrar)
    }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var mensaje by rememberSaveable { mutableStateOf("") }
    if (showDialog) {
        MiAviso(
            show = true,
            texto = mensaje,
            cerrar = {
                showDialog = false
            }
        )
    }


    /** IMAGENES **/

    var tempPhotoUri by remember { mutableStateOf(value = Uri.EMPTY) }

    /** Lanzadores **/
    //Lanza la camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                null  //viewModel.onImageUriChanged(bitmap)
            }
        }
    )
    //Lanza la galeria
    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri != null) {
                viewModel.onImageUriChanged(uri)
            }
        }
    )

    /** Funciones para llamar a los lanzadores **/
    //Al presionar el boton de la camara:
    val tomarFoto = {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if(granted){

            cameraLauncher.launch(null)
        }

        else {
            mensaje = "Algunos permisos se han denegado. Acéptelos desde los ajustes del dispositivo."
            showDialog = true
        }
    }

    //Al presionar el boton de la galeria:
    val elegirImagen = {
        val granted = permisosAlmacenamiento.firstOrNull {
            ContextCompat.checkSelfPermission(
                context,
                it
            ) != PackageManager.PERMISSION_GRANTED
        }
        if(granted.isNullOrEmpty()) {
            singleImagePickerLauncher.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
        else {
            mensaje = "Algunos permisos se han denegado. Acéptelos desde los ajustes del dispositivo."
            showDialog = true
        }
    }
    /******************************************************/

    BalanceContent(
        innerPadding = innerPadding,
        hojaDeGastos = balanceState.hojaAMostrar,
        balanceDeuda = balanceState.balanceDeuda,
        pagoRealizado = balanceState.pagoRealizado,
        onPagoRealizadoChanged = viewModel::onPagoRealizadoChanged,
        pagarDeuda = viewModel::pagarDeuda,
        tomarFoto = { tomarFoto() },
        elegirImagen = { elegirImagen() },
        imagenUri = balanceState.imagenUri,
        listPagos = balanceState.listaPagosConParticipantes,
        recargarDatos = viewModel::updateIfHojaBalanceada
    )
}


@Composable
fun BalanceContent(
    innerPadding: PaddingValues?,
    hojaDeGastos: HojaConParticipantes?,
    balanceDeuda: Map<String, Double>?,
    pagoRealizado: Boolean,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    pagarDeuda: (Pair<String, Double>?) -> Unit,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit,
    imagenUri: Uri?,
    listPagos: List<PagoConParticipantes>?,
    recargarDatos: () -> Unit
){
    var showBalance by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding!!)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {

            DatosHoja(hojaDeGastos)

            /** BOTON PARA DESPLEGAR RESOLUCION **/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Icon(
                    imageVector= Icons.Default.Refresh ,
                    contentDescription = "Recargar datos",
                    tint = if(showBalance) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { recargarDatos() }
                )
                Row(
                    Modifier
                        .clickable { showBalance = !showBalance },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Icon(
                        imageVector= if(showBalance) Icons.Default.ArrowCircleUp else Icons.Default.ArrowCircleDown,
                        contentDescription = "Flecha de apertura/cierre",
                        tint = if(showBalance) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Balance",
                        color = if(showBalance) Color.Black else MaterialTheme.colorScheme.primary
                    )
                }
            }

            LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                if (balanceDeuda?.isNotEmpty() == true) {

                    /** RECUADRO CON ACCIONES DE RESOLUCION **/
                    item {
                        AnimatedVisibility(
                            visible = showBalance,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 15.dp)
                            ) {
                                ResolucionBox(
                                    balanceDeuda = balanceDeuda,
                                    pagoRealizado = pagoRealizado,
                                    onPagoRealizadoChanged = onPagoRealizadoChanged,
                                    pagarDeuda = pagarDeuda,
                                    tomarFoto = tomarFoto,
                                    elegirImagen = elegirImagen,
                                    imagenUri = imagenUri
                                )
                            }
                        }
                    }

                    /** LISTA CON LOS PARTICIPANTES Y SU BALANCE **/
                    item {
                        Text(text = "Resultado:")
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            items(
                                balanceDeuda.toList(), key = { it.first }) { (nombre, monto) ->
                                BalanceDesing(
                                    participante = nombre,
                                    monto = monto,
                                    paddVert = 10
                                )
                            }
                        }
                    }

                    /** LISTA DE PAGOS **/
                    if (!listPagos.isNullOrEmpty()) {
                        item {
                            Spacer(modifier = Modifier.size(25.dp))
                            Text(text = "Pagos:")
                        }

                        items(listPagos, key = { it.monto }) { pago ->
                            PagoDesing( pago )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DatosHoja(hojaDeGastos: HojaConParticipantes?){
    Surface(
        shape = RoundedCornerShape(1.dp),
        elevation = 2.dp,
        modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 5.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row {
            Column {
                Row(
                    modifier = Modifier
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
                        Row {
                            Text(
                                text = when (hojaDeGastos?.hoja?.status) {
                                    "C" -> "Activa"
                                    "A" -> "Anulada"
                                    "B" -> "Balanceada"
                                    else -> "Finalizada"
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
                                text = if (hojaDeGastos?.hoja?.limite.isNullOrEmpty()) "-" else hojaDeGastos?.hoja?.limite.toString(),
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
                    if (hojaDeGastos?.hoja?.status == "B"){
                        Image(
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSecondaryContainer),
                            painter = painterResource(id = R.drawable.balanceada),
                            modifier = Modifier
                                .size(40.dp),
                            contentDescription = "Hoja Balanceada",
                        )
                    }
                }
            }
        }
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
                .padding(18.dp)
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
fun PagoDesing(
    pago: PagoConParticipantes,
){
    val context = LocalContext.current
    //Aviso de la opcion elegida:
    val showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    var showFoto by rememberSaveable { mutableStateOf(false)}
    val currencyFormatter = NumberFormat.getCurrencyInstance()

    if (showDialog){
        Toast.makeText(context, "${pago.nombreAcreedor} no ha confirmado aun.", Toast.LENGTH_SHORT).show()
    }

    if (showFoto){
        if (pago.fotoPago != null) {
            MiImagenDialog(
                show = true,
                imagen =  pago.fotoPago,
                cerrar = { showFoto = false }
            )
        }
        else Toast.makeText(context, "No hay foto adjuntada al pago.", Toast.LENGTH_SHORT).show()
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        elevation = 15.dp,
        modifier = Modifier
            .padding(vertical = 14.dp, horizontal = 12.dp)
            .fillMaxWidth()

    ) {
        Column(
            modifier = Modifier
                .width(180.dp)
                .padding(vertical = 14.dp, horizontal = 17.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = pago.fechaPago,
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                modifier = Modifier
                    .padding(bottom = 10.dp)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row{
                    Text(
                        text = "De ${pago.nombrePagador} ",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 17.sp
                    )
                }
                Row {
                    Text(
                        text = "a ${pago.nombreAcreedor}",
                        style = MaterialTheme.typography.titleSmall,
                        fontSize = 17.sp
                    )
                }
                Text(
                    text = currencyFormatter.format(pago.monto),
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Start
            ) {
                /** FOTO **/
                Card(
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(Color.Transparent),
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .clickable { showFoto = true}
                ) {
                    Icon(
                        Icons.Default.Photo,
                        contentDescription = "Iconos de la camara",
                        tint = if (pago.fotoPago != null) MaterialTheme.colorScheme.primary else Color.DarkGray,
                        modifier = Modifier.size(30.dp)
                    )
                }
                /** CONFIRMACION **/
                Card(
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(Color.Transparent),
                    modifier = Modifier
                        .padding(start = 70.dp)
                        .clickable {
                            Toast
                                .makeText(
                                    context,
                                    "${pago.nombreAcreedor} no ha confirmado aun.",
                                    Toast.LENGTH_SHORT
                                )
                                .show()
                        },
                ) {
                    Icon(
                        Icons.Filled.Handshake,
                        contentDescription = "Iconos de confirmacion del pago",
                        tint = if (pago.confirmado) Color.Green else Color.DarkGray,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ResolucionBox(
    balanceDeuda: Map<String, Double>?,
    pagoRealizado: Boolean,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    pagarDeuda: (Pair<String, Double>?) -> Unit,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit,
    imagenUri: Uri?
) {
    val montoRegistrado = balanceDeuda!!.firstNotNullOf { it.value } //mi monto
    val context = LocalContext.current
    var titulo by rememberSaveable { mutableStateOf("") } //Titulo a mostrar
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar
    var opcionSeleccionada by rememberSaveable { mutableStateOf<Pair<String, Double>?>(null) }

    LaunchedEffect(pagoRealizado) {
        if (pagoRealizado) {
            opcionSeleccionada = null
            onPagoRealizadoChanged(false)
        }
    }

    //AVISO PARA MOSTRAR LOS ACREEDORES A PAGAR:
    var showDialogWhitOptions by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialogWhitOptions) balanceDeuda.let { listaParticipantes ->
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
    if (showConfirm) {
        MiDialogo(show = true,
            titulo = titulo,
            mensaje = mensaje,
            cerrar = { showConfirm = false },
            aceptar = {
                pagarDeuda(opcionSeleccionada)
                showConfirm = false
                Toast.makeText(
                    context,
                    "Enviado mensaje de pago a ${opcionSeleccionada?.first}",
                    Toast.LENGTH_SHORT
                ).show()
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

    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outline,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(7.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
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

                /** PASO 1: ELEGIR ACREEDOR **/
                Paso1(
                    montoRegistrado,
                    opcionSeleccionada,
                    { nuevoTitulo -> titulo = nuevoTitulo },
                    { nuevoMensaje -> mensaje = nuevoMensaje },
                    { mostrarDialogo -> showDialogWhitOptions = mostrarDialogo}
                )

            }
            if (montoRegistrado < 0) {
                /** PASO 2: COMPROBANTE **/
                Paso2(
                    tomarFoto,
                    elegirImagen,
                    imagenUri
                )

                /** PASO 3: ENVIAR **/
                Paso3(
                    opcionSeleccionada,
                    { nuevoTitulo -> titulo = nuevoTitulo },
                    { nuevoMensaje -> mensaje = nuevoMensaje },
                    montoRegistrado,
                    { mostrarConfirmacion -> showConfirm = mostrarConfirmacion  }
                )
            }
        }
    }
}


@Composable
fun Paso1(
    montoRegistrado: Double,
    opcionSeleccionada: Pair<String, Double>?,
    onTituloChanged: (String) -> Unit,
    onMensajeChanged: (String) -> Unit,
    mostrarDialogo: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Text(
            text = if (montoRegistrado > 0) "Solicitar el pago.." else if (montoRegistrado < 0) "1 - Pagar a..." else "Saldado",
            fontWeight = FontWeight.Black,
            style = MaterialTheme.typography.bodyMedium
        )
        if (montoRegistrado != 0.0) {


            if (opcionSeleccionada != null) {
                Text(
                    text = opcionSeleccionada.first,
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
                                onTituloChanged("PAGAR A..")
                                onMensajeChanged("(Solo se descontará tu parte de la deuda)")
                                mostrarDialogo(true)
                            }
                        },
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    Icons.Filled.People,
                    "Elegir Participante",
                    tint = MaterialTheme.colorScheme.primary,
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
                                onTituloChanged("PAGAR A..")
                                onMensajeChanged("(Solo se descontará tu parte de la deuda)")
                                mostrarDialogo(true)
                            }
                        }
                )

            }
        }
        if (opcionSeleccionada?.second != null) {
            Text(
                text = if (opcionSeleccionada.second > abs(montoRegistrado)) NumberFormat.getCurrencyInstance().format(
                    abs(montoRegistrado)
                ) else NumberFormat.getCurrencyInstance().format(opcionSeleccionada.second),
                fontSize = 14.sp
            )
        } else Spacer(Modifier.width(30.dp))
    }
}

@Composable
fun Paso2(
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit,
    imagenUri: Uri?
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row {
            Text(
                text = "2 - Adjuntar comprobante..",
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
}

@Composable
fun Paso3(
    opcionSeleccionada: Pair<String, Double>?,
    titulo: (String) -> Unit,
    mensaje: (String) -> Unit,
    montoRegistrado: Double,
    mostrarConfirmacion: (Boolean) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row {
            Text(
                text = "3 - Enviar",
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
                        titulo("CONFIRMAR")
                        mensaje(
                            "Si acepta se enviará un mensaje a ${opcionSeleccionada.first} por el pago de ${
                                if (opcionSeleccionada.second > abs(
                                        montoRegistrado
                                    )
                                ) NumberFormat
                                    .getCurrencyInstance()
                                    .format(
                                        abs(
                                            montoRegistrado
                                        )
                                    ) else NumberFormat
                                    .getCurrencyInstance()
                                    .format(
                                        opcionSeleccionada.second
                                    )
                            }"
                        )
                        mostrarConfirmacion(true)
                    }
                },
            tint = if (opcionSeleccionada != null) MaterialTheme.colorScheme.primary else Color.LightGray
        )
        Spacer(Modifier.width(10.dp))
    }
}