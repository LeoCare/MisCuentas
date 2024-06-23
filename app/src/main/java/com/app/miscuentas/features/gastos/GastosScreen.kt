@file:OptIn(ExperimentalPermissionsApi::class)

package com.app.miscuentas.features.gastos

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.features.MainActivity
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo
import com.app.miscuentas.util.Desing.Companion.MiDialogoWithOptions
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlin.random.Random


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GastosScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Long?,
    onNavNuevoGasto: (Long) -> Unit,
    selectImage: () -> Unit,
    viewModel: GastosViewModel = hiltViewModel()
) {
    val gastosState by viewModel.gastosState.collectAsState()
    val context = LocalContext.current
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()

    val colors = listOf(
        generateRandomColor(),
        generateRandomColor(),
        generateRandomColor(),
        generateRandomColor(),
        generateRandomColor(),
        generateRandomColor()
    )

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

    /** Inicio solicitud de permisos  **/
    //Permisos a solicitar dependiendo de la version
    val permisosRequeridos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO
        )
    } else {
        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    val statePermisoAlmacenamiento = rememberMultiplePermissionsState(
        permissions = permisosRequeridos
    )

    val tomarFoto = {
        //al presionar el boton de la camara:
        viewModel.solicitaPermisos(statePermisoAlmacenamiento)
        if(statePermisoAlmacenamiento.allPermissionsGranted) {
            (context as? MainActivity)?.tomarFoto(context) //Se lanza desde el MAINACTIVITY
        }
        else if(!statePermisoAlmacenamiento.permissions[0].status.isGranted) {
            viewModel.solicitaPermisos(statePermisoAlmacenamiento)
        }
        else {
            mensaje = "Acepte los permisos a la camara e imagenes desde los ajustes del dispositivo."
            showDialog = true
        }
    }

    /** ***Fin solicitud de permisos** */

    //Hoja a mostrar pasada por el Screen Hojas
    LaunchedEffect(Unit) {
            viewModel.onHojaAMostrar(idHojaAMostrar)
    }

    //Borrar un gasto o cerrar al hoja
    LaunchedEffect(gastosState){
        if(gastosState.gastoElegido != null){
            viewModel.deleteGasto()
        }
        if(gastosState.cierreAceptado){
            viewModel.updateHoja()
        }
    }

    GastosContent(
        innerPadding,
        gastosState.hojaAMostrar,
        listaIconosGastos,
        { onNavNuevoGasto(it) },
        { viewModel.onBorrarGastoChanged(it) },
        gastosState.permisoCamara,
        { tomarFoto()},
        { selectImage() },
        gastosState.pagoRealizado,
        gastosState.existeRegistrado,
        gastosState.resumenGastos,
        gastosState.balanceDeuda,
        { viewModel.obtenerParticipantesYSumaGastos() },
        { viewModel.calcularBalance() },
        { viewModel.onCierreAceptado(it) },
        { viewModel.onPagoRealizadoChanged(it) },
        { viewModel.pagarDeuda(it) },
        gastosState.listaPagosConParticipantes,
        statePermisoAlmacenamiento,
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
    permisoCamara: Boolean,
    tomarFoto: () -> Unit,
    selectImage: () -> Unit,
    pagoRealizado: Boolean,
    existeRegistrado: Boolean,
    resumenGastos: Map<String, Double>?,
    balanceDeuda: Map<String, Double>?,
    obtenerParticipantesYSumaGastos: () -> Unit,
    calcularBalance: () -> Unit,
    onCierreAceptado: (Boolean) -> Unit,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    pagarDeuda: (Pair<String, Double>?) -> Unit,
    listPagos: List<PagoConParticipantes>?,
    statePermisoAlmacenamiento: MultiplePermissionsState,
    colors: List<Color>
){
    val isEnabled = hojaDeGastos?.hoja?.status == "C" //para controlar el boton de 'Agregar Gasto'
    var showResumen by rememberSaveable { mutableStateOf(false) }
    var showBalance by rememberSaveable { mutableStateOf(false) }

    //Control al presionarl 'Balance'
    var showDialog by rememberSaveable { mutableStateOf(false) }
    if (showDialog) MiAviso(
        show = true,
        texto = "Para realizar el Balance, primero FINALIZA la hoja.",
        cerrar = { showDialog = false },
    )

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
                ){
                    Column{
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 7.dp),
                            horizontalArrangement = Arrangement.spacedBy(180.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = hojaDeGastos?.hoja?.titulo ?: "aun nada" ,
                                style = MaterialTheme.typography.headlineLarge,
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
                            }
                            Column {
                                ImagenCierre(hojaDeGastos, { onCierreAceptado(it) })
                            }
                        }
                    }
                }
            }

            /** FILA CON RESUMEN Y BALANCE **/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){

                //RESUMEN:
                Column(
                    Modifier
                        .clickable {
                            obtenerParticipantesYSumaGastos()
                            calcularBalance()
                            showResumen = !showResumen
                            if(showBalance) showBalance = false
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        Icons.Default.AccountBox,
                        contentDescription = "Icono de participantes",
                        tint = if(showResumen) Color.Black else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Resumen",
                        color = if(showResumen) Color.Black else MaterialTheme.colorScheme.primary
                    )
                }
                //BALANCE:
                Column(
                    Modifier
                        .clickable {
                            if (hojaDeGastos?.hoja?.status == "C"){
                                showDialog = true
                            }
                            else {
                                calcularBalance()
//                                getPagos()
                                showBalance = !showBalance
                                if (showResumen) showResumen = false
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        Icons.Default.Payments,
                        contentDescription = "Iconos de pagos",
                        tint = if(showBalance) Color.Black else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                    Text(
                        style = MaterialTheme.typography.titleMedium,
                        text = "Balance",
                        color = if(showBalance) Color.Black else MaterialTheme.colorScheme.primary
                    )
                }
            }

            /** LISTA DEL RESUMEN **/
            AnimatedVisibility(
                visible = showResumen,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                ) {
                    Resumen(
                        balanceDeuda = balanceDeuda,
                        participantes = resumenGastos,
                        onParticipanteClick = {},
                        colors = colors)
                }
            }

            /**  LISTA DEL BALANCE **/
            AnimatedVisibility(
                visible = showBalance,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 15.dp)
                ) {
                    hojaDeGastos?.hoja?.status?.let {statusHoja ->
                        Balance(
                            statePermisoAlmacenamiento = statePermisoAlmacenamiento,
                            permisoCamara = permisoCamara,
                            pagoRealizado = pagoRealizado,
                            exiteRegistrado = existeRegistrado,
                            participantes = balanceDeuda,
                            tomarFoto = tomarFoto,
                            selectImage = selectImage,
                            pagarDeuda = pagarDeuda,
                            onPagoRealizadoChanged = onPagoRealizadoChanged,
                            listPagos = listPagos
                        )
                    }
                }
            }

            /**  LISTA DE GASTOS **/
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                contentPadding = innerPadding,
                modifier = Modifier
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
                                    { onBorrarGastoChanged(it) },
                                    hojaDeGastos.hoja.status
                                )
                            }
                        }

                    }
                }
            }
        }
        CustomFloatButton(
            onNavNuevoGasto = { onNavNuevoGasto(hojaDeGastos?.hoja?.idHoja!!) },
            modifier = Modifier.align(Alignment.BottomCenter), // Alinear el botón en la esquina inferior derecha
            isEnabled = isEnabled,
            showBalance = {
                showBalance = false
                showResumen = false
            }
        )
    }
}

@Composable
fun DatosHoja(hojaDeGastos: HojaConParticipantes?){
    Row {
        when (hojaDeGastos?.hoja?.status) { //pinta segun valor status de la BBDD
            "C" ->
                Text(
                    text = "Activa",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )

            "A" ->
                Text(
                    text = "Anulada",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.error
                )

            "B" ->
                Text(
                    text = "Balanceada",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.error
                )

            else -> Text(
                text = "Finalizada",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 17.sp
            )
        }
    }
    Row {

        Text(
            text = "Fecha Cierre: ",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = hojaDeGastos?.hoja?.fechaCierre ?: "-",
            style = MaterialTheme.typography.labelLarge
        )
    }
    Row {
        Text(
            text = "Limite: ",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = if(hojaDeGastos?.hoja?.limite.isNullOrEmpty()) "-" else hojaDeGastos?.hoja?.limite.toString(),
            style = MaterialTheme.typography.labelLarge
        )
    }
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        Text(
            text = "Participantes: ",
            style = MaterialTheme.typography.labelLarge
        )
        Text(
            text = hojaDeGastos?.participantes?.size.toString(),
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun ImagenCierre(
    hojaDeGastos: HojaConParticipantes?,
    onCierreAceptado: (Boolean) -> Unit
){

    var opcionAceptada by rememberSaveable { mutableStateOf(false) }
    if(opcionAceptada) {
        onCierreAceptado(true)
        opcionAceptada = false
    }

    var showDialog by rememberSaveable { mutableStateOf(false) }
    if (showDialog) MiDialogo(
        show = true,
        titulo = "ATENCION!",
        mensaje = "Estas a punto de CERRAR la hoja. Si aceptas, ya no se podrá introcudir mas gastos y podras realizar el Balance.",
        cerrar = { showDialog = false },
        aceptar = {
            opcionAceptada = true
            showDialog = false
        }
    )

    Button(
        enabled = (hojaDeGastos?.hoja?.status == "C"),
        onClick = { showDialog = true },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = Color.Transparent,
            disabledBackgroundColor = Color.Transparent,
            disabledContentColor = Color.Transparent
        ),
        elevation = ButtonDefaults.elevation(0.dp),
        shape = MaterialTheme.shapes.small)
    {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .height(45.dp)
                    .width(45.dp),
                painter = painterResource(id = R.drawable.cerrar),
                contentDescription = "Cierre de la Hoja")
            Text(
                text= "Finalizar",
                style = MaterialTheme.typography.labelLarge,
                fontSize = 14.sp
            )
        }
    }
}


@Composable
fun GastoDesing(
    gasto: DbGastosEntity?,
    participante: ParticipanteConGastos,
    listaIconosGastos: List<IconoGasto>,
    onBorrarGastoChanged: (DbGastosEntity?) -> Unit,
    statusHoja: String
) {
    //Aviso de la opcion elegida:
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo

    if (showDialog){
        if(statusHoja == "F"){
           MiAviso(
               show = true,
               texto = "La hoja esta finalizada, no se puede eliminar",
               cerrar = { showDialog = false }
           )
        }else{
            MiDialogo(
                show = true,
                titulo = "ELIMINAR GASTO",
                mensaje = "Si acepta, se eliminara y no se podrá recuperar.",
                cerrar = { showDialog = false },
                aceptar = {
                    onBorrarGastoChanged(gasto)
                    showDialog = false
                }
            )
        }
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        elevation = 12.dp,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 12.dp)
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
fun SumaPorParticipanteDesing(
    nombre: String,
    sumaGastos: Double,
    color: Color,
    onClick: () -> Unit) {
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
            Text(text = String.format("%.2f €",sumaGastos), color = Color.White, fontSize = 14.sp, textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun ResumenBalanceDesing(
    participante: String,
    monto: Double,
) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ){
            Column(
                modifier = Modifier.width(170.dp),
                horizontalAlignment = Alignment.Start
            ){
                Row {
                    Text(
                        text = participante,
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp
                    )
                }
            }
            Column(
                modifier = Modifier.width(60.dp),
                horizontalAlignment = Alignment.Start
            ){
                Row {
                    Text(
                        text = if(monto > 0) "Recibe" else if(monto < 0) "Debe" else "Saldado",
                        fontSize = 14.sp,
                        color = if(monto > 0) MaterialTheme.colorScheme.onSecondaryContainer else if(monto < 0) Color.Red else Color.Black
                    )
                }
            }
            Column(
                modifier = Modifier.width(130.dp),
                horizontalAlignment = Alignment.End
            ){
                Row  {
                    Text(
                        text = String.format("%.2f €", monto),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

        }
    }
}

@Composable
fun Resumen(
    balanceDeuda: Map<String, Double>?,
    participantes: Map<String, Double>?,
    onParticipanteClick: (String) -> Unit,
    colors: List<Color>
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn {
            participantes?.keys?.chunked(3)?.forEach { fila ->
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items(fila) { participante ->
                            val colorIndex =
                                (participantes.keys.indexOf(participante) % colors.size)
                            val color = colors[colorIndex]
                            SumaPorParticipanteDesing(
                                nombre = participante,
                                sumaGastos = participantes[participante] ?: 0.0,
                                color = color,
                                onClick = { onParticipanteClick(participante) }
                            )
                        }
                    }
                }
            }
            balanceDeuda?.let {
                items(balanceDeuda.toList()) { (nombre, monto) ->
                    ResumenBalanceDesing(
                        participante = nombre,
                        monto = monto,
                    )
                }
            }
        }
    }
}
/*****************************************/


/** BALANCE **/
@Composable
fun PagoDesing(
    permisoCamara: Boolean,
    pago: PagoConParticipantes
){
    val context = LocalContext.current
    //Aviso de la opcion elegida:
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo

    if (showDialog){
        Toast.makeText(context, "${pago.nombreAcreedor} no ha confirmado aun.", Toast.LENGTH_SHORT).show()
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        elevation = 20.dp,
        modifier = Modifier
            .padding(vertical = 4.dp, horizontal = 12.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
                .height(80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Text(text = "De")
                    Text(
                        text = " ${pago.nombrePagador} ",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp
                    )
                }
                Row{
                    Text(text =  String.format("%.2f €", pago.monto), fontWeight = FontWeight.Bold)
                }
            }
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.Start
            ) {
                Row {
                    Text(text = "a")
                    Text(
                        text = " ${pago.nombreAcreedor}",
                        style = MaterialTheme.typography.titleMedium,
                        fontSize = 20.sp
                    )

                }
                Row {
                    Text(text =  pago.fechaPago)
                }

            }
            Row(
                modifier = Modifier
                    .width(100.dp)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Card(
                    elevation = CardDefaults.cardElevation(2.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(Color.Transparent),
                    modifier = Modifier.clickable {
                        if (permisoCamara){}
                        else{ Toast.makeText(context, "El permiso se ha denegado anteriormente.\nConcede el permiso desde los ajustes del telefono.", Toast.LENGTH_SHORT).show() }},
                ) {
                    Icon(
                        Icons.Default.Photo,
                        contentDescription = "Iconos de la camara",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Card(
                    elevation = CardDefaults.cardElevation(0.dp),
                    shape = MaterialTheme.shapes.small,
                    colors = CardDefaults.cardColors(Color.Transparent),
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "${pago.nombreAcreedor} no ha confirmado aun.", Toast.LENGTH_SHORT).show() },
                ) {
                    Icon(
                        Icons.Filled.Handshake,
                        contentDescription = "Iconos de confirmacion del pago",
                        tint = if(pago.confirmado) MaterialTheme.colorScheme.primary else Color.DarkGray,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Balance(
    statePermisoAlmacenamiento: MultiplePermissionsState,
    permisoCamara: Boolean,
    pagoRealizado: Boolean,
    exiteRegistrado: Boolean,
    participantes: Map<String, Double>?,
    tomarFoto: () -> Unit,
    selectImage: () -> Unit,
    pagarDeuda: (Pair<String, Double>?) -> Unit,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    listPagos: List<PagoConParticipantes>?
) {
    val montoRegistrado = participantes!!.firstNotNullOf { it.value } //mi monto
    val context = LocalContext.current
    var titulo by rememberSaveable { mutableStateOf("") } //Titulo a mostrar
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar
    var opcionSeleccionada by rememberSaveable { mutableStateOf<Pair<String, Double>?>(null) }

    LaunchedEffect(pagoRealizado) {
        if (pagoRealizado){
            opcionSeleccionada = null
            onPagoRealizadoChanged(false)
        }
    }

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

    //Este aviso se lanzara cuando se deniega el permiso...
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

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            if (participantes.isNotEmpty()){

                /** LISTA CON LOS PARTICIPANTES Y SU BALANCE **/
                itemsIndexed(participantes.toList()) { _index, (nombre, monto) ->
                    ResumenBalanceDesing(
                        participante = nombre,
                        monto = monto
                    )
                }
                /** RECUADRO CON LAS ACCIONES (SOLICITO O PAGO) **/
                item{
                    if(exiteRegistrado && montoRegistrado != 0.0) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.outline,
                                contentColor = Color.Black
                            ),
                            elevation = CardDefaults.cardElevation(4.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp)
                            ){
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier
                                        .padding(bottom = 5.dp)
                                        .fillMaxWidth()
                                ) {
                                    Row {
                                        Text(text = "1 - ")
                                        Text(
                                            text = if (montoRegistrado > 0) "SOLICITAR EL PAGO A.." else if (montoRegistrado < 0) "PAGAR A.." else "LISTO",
                                            fontSize = 14.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
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
                                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                                    ) {
                                        Text(
                                            text =
                                            if(opcionSeleccionada != null) opcionSeleccionada!!.first
                                            else if (montoRegistrado > 0) "Todos" else if (montoRegistrado < 0) "Elegir" else "Listo",
                                            fontSize = 15.sp,
                                            color = Color.White
                                        )
                                    }
                                    opcionSeleccionada?.let {elegido ->
                                        Text(
                                            text = String.format("%.2f €", elegido.second),
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                                if (montoRegistrado < 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 5.dp)
                                    ) {
                                        Text(text = "2 - ")
                                        IconButton(onClick = {
                                            tomarFoto()
                                        }) {
                                            Icon(
                                                Icons.Default.PhotoCamera,
                                                contentDescription = "Tomar foto",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        IconButton(onClick = {
                                            selectImage()
                                        }) {
                                            Icon(
                                                Icons.Default.PhotoLibrary,
                                                contentDescription = "Galeria",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Text(
                                            text = "Comprobante de pago",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 5.dp)
                                    ) {
                                        Text(text = "3 - ")
                                        Button(
                                            enabled = (opcionSeleccionada != null),
                                            onClick = {
                                                titulo = "CONFIRMAR"
                                                mensaje = "Si acepta se enviará un mensaje a.. ${opcionSeleccionada?.first} por el pago de ${opcionSeleccionada?.second}"
                                                showConfirm = true

                                            },
                                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                                            content = {
                                                Text(text = "Enviar aviso", color = Color.White)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                /** LISTA DE PAGOS **/
                item{
                    Divider(
                        modifier = Modifier.padding(vertical = 20.dp),
                        color  = Color.LightGray
                    )
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp),
                        text = "PAGOS:",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                items(listPagos!!) {pago ->
                    PagoDesing(
                        permisoCamara,
                        pago
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

    FloatingActionButton(
        onClick = {
            showBalance()
            if (isEnabled) onNavNuevoGasto()
            else showDialog = true
        },
        elevation = FloatingActionButtonDefaults.elevation(13.dp),
        modifier = modifier
            .height(120.dp)
            .width(80.dp)
            .padding(bottom = 54.dp, end = 14.dp), // Añade el padding al botón flotante
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

/*************************/
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
