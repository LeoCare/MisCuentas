package com.app.miscuentas.features.balance

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WavingHand
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.local.dbroom.entitys.DbPagoEntity
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo
import com.app.miscuentas.util.Desing.Companion.MiDialogoWithOptions
import com.app.miscuentas.util.Desing.Companion.MiDialogoWithOptions2
import com.app.miscuentas.util.Desing.Companion.MiImagenDialog
import com.app.miscuentas.util.Imagen.Companion.permisosAlmacenamiento
import com.app.miscuentas.util.Imagen.Companion.uriToBitmap
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
        viewModel.getIdRegistroPreference()
        viewModel.onHojaAMostrar(idHojaAMostrar)
    }

    LaunchedEffect(balanceState.opcionSelected){
        when(balanceState.opcionSelected) {
            "S" -> { viewModel.ConfirmarPago() }
        }
    }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    var mensaje by rememberSaveable { mutableStateOf("") }
    if (showDialog) {
        MiAviso(
            show = true,
            titulo = "IMPORTANTE",
            mensaje = mensaje,
            cerrar = {
                showDialog = false
            }
        )
    }

    var showFoto by rememberSaveable { mutableStateOf(false)}
    if (showFoto){
        MiImagenDialog(
            show = true,
            imagen = balanceState.imagenBitmap!!,
            cerrar = {
                showFoto = false
                viewModel.onImagenBitmapChanged(null)
            }
        )
    }

    /** IMAGENES **/
    /** Lanzadores **/
    //Lanza la camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            if (bitmap != null) {
                if (balanceState.pagoNewFoto != null) {
                    viewModel.insertNewImage(bitmap)
                }
                else {
                    viewModel.onImagenBitmapChanged(bitmap)
                }
            }
        }
    )

    //Lanza la galeria
    val singleImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                val bitmap = uriToBitmap(context, uri)
                if (bitmap != null) {
                    if (balanceState.pagoNewFoto != null) {
                        viewModel.insertNewImage(bitmap)
                    }
                    else {
                        viewModel.onImagenBitmapChanged(bitmap)
                    }
                }
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
        idRegistrado = balanceState.idRegistrado,
        idPartiRegistrado = balanceState.idPartiRegistrado,
        hojaDeGastos = balanceState.hojaAMostrar,
        balanceDeuda = balanceState.balanceDeuda,
        pagoRealizado = balanceState.pagoRealizado,
        onPagoRealizadoChanged = viewModel::onPagoRealizadoChanged,
        pagarDeuda = viewModel::pagarDeuda,
        solicitudPago = viewModel::solicitudPago,
        imagenBitmapState = balanceState.imagenBitmap,
        tomarFoto = { tomarFoto() },
        elegirImagen = { elegirImagen() },
        onNewFotoPagoChanged = viewModel::onNewFotoPagoChanged,
        listPagos = balanceState.listaPagosConParticipantes,
        recargarDatos = viewModel::updateIfHojaBalanceada,
        onOpcionSelectedChanged = viewModel::onOpcionSelectedChanged,
        onPagoAModificarChanged = viewModel::onPagoAModificarChanged
    )
}


@Composable
fun BalanceContent(
    innerPadding: PaddingValues?,
    idRegistrado: Long,
    idPartiRegistrado: Long,
    hojaDeGastos: HojaConParticipantes?,
    balanceDeuda: Map<DbParticipantesEntity, Double>?,
    pagoRealizado: Boolean,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    pagarDeuda: (Pair<DbParticipantesEntity, Double>?, Pair<DbParticipantesEntity, Double>?) -> Unit,
    solicitudPago: (DbParticipantesEntity) -> Unit,
    imagenBitmapState: Bitmap?,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit,
    onNewFotoPagoChanged: (PagoConParticipantes) -> Unit,
    listPagos: List<PagoConParticipantes>?,
    recargarDatos: () -> Unit,
    onOpcionSelectedChanged: (String) -> Unit,
    onPagoAModificarChanged: (PagoConParticipantes) -> Unit
){
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

            /** BOTON PARA RECARGAR LA HOJA **/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                Icon(
                    imageVector= Icons.Default.Refresh ,
                    contentDescription = "Recargar datos",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable { recargarDatos() }
                )
            }
            LazyColumn(modifier = Modifier.padding(horizontal = 8.dp)) {
                if (balanceDeuda?.isNotEmpty() == true) {
                    /** LISTA CON LOS PARTICIPANTES Y SU BALANCE **/
                    item {
                        Text(text = "Resultado:")
                        LazyRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(
                                balanceDeuda.toList(), key = { it.first.idParticipante }) {
                                BalanceDesing(
                                    idRegistrado = idRegistrado,
                                    participante = it.first,
                                    monto = it.second,
                                    balanceDeuda = balanceDeuda,
                                    pagoRealizado = pagoRealizado,
                                    onPagoRealizadoChanged = onPagoRealizadoChanged,
                                    pagarDeuda = pagarDeuda,
                                    solicitudPago = solicitudPago,
                                    imagenBitmapState = imagenBitmapState,
                                    tomarFoto = tomarFoto,
                                    elegirImagen = elegirImagen
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

                        items(listPagos, key = { it.idPago }) { pago ->
                            PagoDesing(
                                idPartiRegistrado,
                                pago ,
                                tomarFoto,
                                elegirImagen,
                                { onNewFotoPagoChanged(it) },
                                onOpcionSelectedChanged = { onOpcionSelectedChanged(it) },
                                onPagoAModificarChanged =  { onPagoAModificarChanged(pago) }
                            )
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
    idRegistrado: Long,
    participante: DbParticipantesEntity,
    monto: Double,
    balanceDeuda: Map<DbParticipantesEntity, Double>?,
    pagoRealizado: Boolean,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    pagarDeuda: (Pair<DbParticipantesEntity, Double>?, Pair<DbParticipantesEntity, Double>?) -> Unit,
    solicitudPago: (DbParticipantesEntity) -> Unit,
    imagenBitmapState: Bitmap?,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit,
) {
    val currencyFormatter = NumberFormat.getCurrencyInstance()
    var showBalance by rememberSaveable { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(18.dp),
        elevation = 6.dp,
        modifier = Modifier
            .padding(vertical = 10.dp, horizontal = 5.dp)
            .fillMaxWidth()
            .clickable {
                if (participante.tipo == "LOCAL" || participante.idUsuarioParti == idRegistrado) {
                    showBalance = !showBalance
                }
            }
    ) {
        Row (
            modifier = Modifier.fillMaxSize()
        ){
            Column(
                modifier = Modifier
                    .padding(25.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = participante.nombre,
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
                if(monto != 0.0) {
                    AnimatedVisibility(
                        visible = showBalance,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 5.dp)
                        ) {
                            ResolucionBox(
                                participante = participante,
                                balanceDeuda = balanceDeuda,
                                pagoRealizado = pagoRealizado,
                                onPagoRealizadoChanged = onPagoRealizadoChanged,
                                pagarDeuda = pagarDeuda,
                                solicitudPago = solicitudPago,
                                imagenBitmapState = imagenBitmapState,
                                tomarFoto = tomarFoto,
                                elegirImagen = elegirImagen
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    if(participante.tipo == "LOCAL" || participante.idUsuarioParti == idRegistrado){
                        Icon(
                            modifier = Modifier
                                .size(20.dp),
                            tint = MaterialTheme.colorScheme.primary,
                            imageVector = if (showBalance) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = "Pago o Balance",
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun PagoDesing(
    idPartiRegistrado: Long,
    pago: PagoConParticipantes,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit,
    onNewFotoPagoChanged: (PagoConParticipantes) -> Unit,
    onOpcionSelectedChanged: (String) -> Unit,
    onPagoAModificarChanged: () -> Unit
){
    val context = LocalContext.current
    //Aviso de la opcion elegida:
    val showDialog by rememberSaveable { mutableStateOf(false) }
    var showFoto by rememberSaveable { mutableStateOf(false)}
    var showOpciones by rememberSaveable { mutableStateOf(false) }
    var titulo by rememberSaveable { mutableStateOf("") }
    var mensaje by rememberSaveable { mutableStateOf("") }
    var opcionSeleccionada by rememberSaveable { mutableStateOf("") }
    var opcionAceptada by rememberSaveable { mutableStateOf(false) }
    val currencyFormatter = NumberFormat.getCurrencyInstance()


    if (showDialog){
        Toast.makeText(context, "${pago.nombreAcreedor} no ha confirmado aun.", Toast.LENGTH_SHORT).show()
    }

    if (showFoto){
        MiImagenDialog(
            show = true,
            imagen = pago.fotoPago!!,
            cerrar = { showFoto = false }
        )
    }

    if(opcionAceptada) {
        onOpcionSelectedChanged(opcionSeleccionada)
        opcionAceptada = false
    }

    if (showOpciones) {
        val opciones: List<String> = listOf("Si, ya lo tengo", "No, aun no lo he recibido")

        MiDialogoWithOptions2(
            show = true,
            opciones = opciones,
            titulo = titulo,
            mensaje = mensaje,
            cancelar = { showOpciones = false },
            onOptionSelected = {
                opcionSeleccionada = if(it == "Si, ya lo tengo") "S" else "N"
                opcionAceptada = true
                showOpciones = false
            }
        )
    }

    Surface(
        shape = RoundedCornerShape(10.dp),
        elevation = 15.dp,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .width(180.dp)
                .padding(top = 7.dp, start = 17.dp, end = 17.dp)
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
                /** CONFIRMACION **/
                if (idPartiRegistrado == pago.idPartiAcreedor &&  pago.fechaConfirmacion.isNullOrEmpty()) {
                    Card(
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                titulo = "CONFIRMACION DE PAGO RECIBIDO"
                                mensaje = "¿Has recibido el pago indicado?"
                                onPagoAModificarChanged()
                                showOpciones = true
                            }) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Debes confirmar",
                            tint = MaterialTheme.colorScheme.scrim,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                } else {
                    Card(
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                Toast
                                    .makeText(
                                        context,
                                        "${pago.nombreAcreedor} ha confirmado el pago.",
                                        Toast.LENGTH_SHORT
                                    )
                                    .show()
                            }) {
                        Icon(
                            Icons.Filled.Handshake,
                            contentDescription = "Iconos de confirmacion del pago",
                            tint = if (pago.fechaConfirmacion != null)  MaterialTheme.colorScheme.onSecondaryContainer else Color.DarkGray,
                            modifier = Modifier.size(25.dp)
                        )
                    }

                }
            }
            Row(
                modifier = Modifier
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
            /** LISTA DE OPCIONES **/
            OpcionesPago(
                pago.fotoPago
            ) { opcion ->
                when(opcion) {
                    "Camara" ->  {
                        onNewFotoPagoChanged(pago)
                        tomarFoto()
                    }

                    "Galeria" ->  {
                        onNewFotoPagoChanged(pago)
                        elegirImagen()
                    }

                    "Ver" ->  {
                        if (pago.fotoPago != null) {
                            showFoto = true
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ResolucionBox(
    participante: DbParticipantesEntity,
    balanceDeuda: Map<DbParticipantesEntity, Double>?,
    pagoRealizado: Boolean,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    pagarDeuda: (Pair<DbParticipantesEntity, Double>?, Pair<DbParticipantesEntity, Double>?) -> Unit,
    solicitudPago: (DbParticipantesEntity) -> Unit,
    imagenBitmapState: Bitmap?,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit
) {
    val montoRegistrado = balanceDeuda!![participante] // monto
    val context = LocalContext.current
    var titulo by rememberSaveable { mutableStateOf("") } //Titulo a mostrar
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar
    var opcionSeleccionada by rememberSaveable { mutableStateOf<Pair<DbParticipantesEntity, Double>?>(null) }

    LaunchedEffect(pagoRealizado) {
        if (pagoRealizado) {
            opcionSeleccionada = null
            onPagoRealizadoChanged(false)
        }
    }
    //ENVIAR EMAIL A LOS DEUDORES
    var enviarSolicitud by rememberSaveable { mutableStateOf(false) }
    if (enviarSolicitud){
        solicitudPago(participante)
        enviarSolicitud = false
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
            opciones = listaParticipantesSinPrimero,
            titulo = titulo,
            mensaje = mensaje,
            cancelar = { showDialogWhitOptions = false },
            onOptionSelected = {
                opcionSeleccionada = it
                showDialogWhitOptions = false
            }
        )
    }

    //AVISO CUANDO SE PAGA LA DEUDA:
    var showConfirm by rememberSaveable { mutableStateOf(false) }
    var deudor by rememberSaveable { mutableStateOf<Pair<DbParticipantesEntity, Double>?>(null) }
    deudor = balanceDeuda.entries
        .find { it.key == participante }
        ?.let { Pair(it.key, it.value) }

    if (showConfirm) {
        MiDialogo(show = true,
            titulo = titulo,
            mensaje = mensaje,
            cerrar = { showConfirm = false },
            aceptar = {
                pagarDeuda(deudor, opcionSeleccionada)
                showConfirm = false
                Toast.makeText(
                    context,
                    "Enviado mensaje de pago a ${opcionSeleccionada?.first?.nombre}",
                    Toast.LENGTH_SHORT
                ).show()
            })
    }

    //AVISO CUANDO SE DENIEGA EL PERMISO:
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) {
        MiAviso(
            show = true,
            titulo = "IMPORTANTE",
            mensaje = mensaje,
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
            /** PASO 1: ELEGIR ACREEDOR **/
            Paso1(
                montoRegistrado,
                opcionSeleccionada,
                { nuevoTitulo -> titulo = nuevoTitulo },
                { nuevoMensaje -> mensaje = nuevoMensaje },
                { mostrarDialogo -> showDialogWhitOptions = mostrarDialogo},
                { solicitar -> enviarSolicitud = solicitar}
            )

            if (montoRegistrado != null) {
                if (montoRegistrado < 0) {
                    /** PASO 2: COMPROBANTE **/
                    Paso2(
                        imagenBitmapState,
                        tomarFoto,
                        elegirImagen
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
}


@Composable
fun Paso1(
    montoRegistrado: Double?,
    opcionSeleccionada: Pair<DbParticipantesEntity, Double>?,
    onTituloChanged: (String) -> Unit,
    onMensajeChanged: (String) -> Unit,
    mostrarDialogo: (Boolean) -> Unit,
    solicitar: (Boolean) -> Unit
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(25.dp),

    ) {
        if (montoRegistrado != null) {
            Text(
                text = if (montoRegistrado > 0) "Solicitar el pago.." else if (montoRegistrado < 0) "1 - Pagar a..." else "Saldado",
                style = MaterialTheme.typography.bodyLarge
            )

            if (montoRegistrado != 0.0) {
                if (opcionSeleccionada != null) {
                    Text(
                        text = opcionSeleccionada.first.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .clickable {

                                if (montoRegistrado > 0) {
                                    solicitar(true)
                                    Toast.makeText(
                                        context,
                                        "Se ha solicitado el pago a los deudores",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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
                        imageVector = if (montoRegistrado > 0) Icons.Filled.WavingHand else Icons.Filled.People,
                        "Solicitar el pago",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable {

                                if (montoRegistrado > 0){
                                    solicitar(true)
                                    Toast
                                        .makeText(
                                            context,
                                            "Se ha solicitado el pago a los deudores",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
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
                    style = MaterialTheme.typography.bodyLarge
                )


            } else Spacer(Modifier.width(30.dp))
        }
    }
}

@Composable
fun Paso2(
    imagenBitmapState: Bitmap?,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit
) {

    var showFoto by rememberSaveable { mutableStateOf(false)}
    if (showFoto){
        if (imagenBitmapState != null) {
            MiImagenDialog(
                show = true,
                imagen = imagenBitmapState,
                cerrar = {
                    showFoto = false
                }
            )
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row {
            Text(
                text = "2 - Adjuntar comprobante..",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row {
            /** LISTA DE OPCIONES **/
            OpcionesPago(
                imagenBitmapState
            ) { opcion ->
                when(opcion) {
                    "Camara" ->  {
                        tomarFoto()
                    }

                    "Galeria" ->  {
                        elegirImagen()
                    }

                    "Ver" -> {
                        showFoto = true
                    }
                }
            }
        }
    }
}

@Composable
fun Paso3(
    opcionSeleccionada: Pair<DbParticipantesEntity, Double>?,
    titulo: (String) -> Unit,
    mensaje: (String) -> Unit,
    montoRegistrado: Double,
    mostrarConfirmacion: (Boolean) -> Unit
) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row {
            Text(
                text = "3 - Enviar",
                style = MaterialTheme.typography.bodyLarge
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
                            "Si acepta se enviará un mensaje a ${opcionSeleccionada.first.nombre} por el pago de ${
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

/** OPCIONES ELEGIBLES PARA CADA PAGO **/
@Composable
fun OpcionesPago(
    fotoPago: Bitmap?,
    onOptionSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box {
        IconButton(onClick = {
           expanded = true
        }) {
            Icon(
                imageVector = Icons.Default.Image,
                contentDescription = "Menu de opciones",
                tint = if(fotoPago != null) MaterialTheme.colorScheme.primary else Color.LightGray
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (fotoPago != null) {
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onOptionSelected("Ver")
                    }
                ) {
                    Text("Ver")
                }
            }

            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onOptionSelected("Camara")
                }
            ) {
                Text("Camara")
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onOptionSelected("Galeria")
                }
            ) {
                Text("Galeria")
            }
        }
    }
}