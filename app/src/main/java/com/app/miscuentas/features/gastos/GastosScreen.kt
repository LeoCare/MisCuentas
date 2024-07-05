@file:OptIn(ExperimentalPermissionsApi::class)

package com.app.miscuentas.features.gastos

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.PagoConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Desing.Companion.MiDialogo
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.File
import java.text.NumberFormat


@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun GastosScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Long?,
    onNavNuevoGasto: (Long) -> Unit,
    onNavBalance: (Long) -> Unit,
    viewModel: GastosViewModel = hiltViewModel()
) {
    val gastosState by viewModel.gastosState.collectAsState()
    val context = LocalContext.current
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()
    
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
    /**************************/

    /** GALERIA DE IMAGENES **/
    val singleImagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            viewModel.onImageUriChanged(uri)
            val ruta = viewModel.getPathFromUri(context, uri)
            viewModel.onImageAbsolutePathChanged(ruta)
        }
    }

    val elegirImagen = {
        singleImagePickerLauncher.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        ) }
    /**************************/

    /** CAMARA DE FOTOS **/
    var tempPhotoUri by remember { mutableStateOf(value = Uri.EMPTY) }

    //LANZA LA CAMARA
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                viewModel.onImageUriChanged(tempPhotoUri)
            }
        }
    )

    //CREAR ARCHIVO URI
    fun Context.createTempPictureUri(
        fileName: String = "IMG_${System.currentTimeMillis()}",
        fileExtension: String = ".jpg"
    ): Uri {
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        val tempFile = File(storageDir, "$fileName$fileExtension")
        viewModel.onImageAbsolutePathChanged(tempFile.absolutePath)
        return FileProvider.getUriForFile(this, "${packageName}.provider", tempFile)
    }

    //AL PRESIONAR EL BOTON DE LA CAMARA:
    val tomarFoto = {
        viewModel.solicitaPermisos(statePermisoAlmacenamiento)
        if(statePermisoAlmacenamiento.allPermissionsGranted) {
            tempPhotoUri = context.createTempPictureUri()
            cameraLauncher.launch(tempPhotoUri)
        }
        else if(!statePermisoAlmacenamiento.permissions[0].status.isGranted) {
            viewModel.solicitaPermisos(statePermisoAlmacenamiento)
        }
        else {
            mensaje = "Acepte los permisos a la camara e imagenes desde los ajustes del dispositivo."
            showDialog = true
        }
    }
    /******************************************************/

    //Hoja a mostrar pasada por el Screen Hojas
    LaunchedEffect(Unit) {
        viewModel.onHojaAMostrar(idHojaAMostrar)
    }

    //Borrar un gasto o cerrar al hoja
    LaunchedEffect(gastosState){
        if (gastosState.gastoElegido != null){
            viewModel.deleteGasto()
        }
        if (gastosState.cierreAceptado){
            viewModel.updateHoja()
        }
    }

    GastosContent(
        innerPadding,
        gastosState.hojaAMostrar,
        listaIconosGastos,
        { onNavNuevoGasto(it) },
        { onNavBalance(it) },
        viewModel::onBorrarGastoChanged,
        gastosState.permisoCamara,
        { tomarFoto() },
        { elegirImagen() },
        viewModel::onImageUriChanged,
        gastosState.pagoRealizado,
        gastosState.existeRegistrado,
        gastosState.sumaParticipantes,
        gastosState.balanceDeuda,
        gastosState.imageUri,
        gastosState.imageAbsolutePath,
        viewModel::obtenerFotoPago,
        viewModel::obtenerParticipantesYSumaGastos,
        viewModel::calcularBalance,
        viewModel::onCierreAceptado,
        viewModel::onPagoRealizadoChanged,
        viewModel::pagarDeuda,
        gastosState.listaPagosConParticipantes
    )
}

@Composable
fun GastosContent(
    innerPadding: PaddingValues?,
    hojaDeGastos: HojaConParticipantes?,
    listaIconosGastos: List<IconoGasto>,
    onNavNuevoGasto: (Long) -> Unit,
    onNavBalance: (Long) -> Unit,
    onBorrarGastoChanged: (DbGastosEntity?) -> Unit,
    permisoCamara: Boolean,
    tomarFoto: () -> Unit,
    elegirImagen: () -> Unit,
    onImageUriChanged: (Uri?) -> Unit,
    pagoRealizado: Boolean,
    existeRegistrado: Boolean,
    sumaParticipantes: Map<String, Double>?,
    balanceDeuda: Map<String, Double>?,
    imagenUri: Uri?,
    imagenPago: String?,
    obtenerFotoPago: (Long) -> Unit,
    obtenerParticipantesYSumaGastos: () -> Unit,
    calcularBalance: () -> Unit,
    onCierreAceptado: (Boolean) -> Unit,
    onPagoRealizadoChanged: (Boolean) -> Unit,
    pagarDeuda: (Pair<String, Double>?) -> Unit,
    listPagos: List<PagoConParticipantes>?
){
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

                            DatosHoja(hojaDeGastos)

                            if (hojaDeGastos?.hoja?.status != "F"){
                                Column {
                                    ImagenCierre(hojaDeGastos, { onCierreAceptado(it) })
                                }
                            }

                        }
                    }
                }
            }

            /** BOTON PARA DESPLEGAR EL RESUMEN: **/
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(end = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ){
                Row(
                    Modifier
                        .clickable {
                            if(!showResumen) {
                                obtenerParticipantesYSumaGastos()
                                calcularBalance()
                            }
                            showResumen = !showResumen
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Icon(
                        Icons.Default.AccountBox,
                        contentDescription = "Icono de participantes",
                        tint = if(showResumen) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                    Text(
                        style = MaterialTheme.typography.bodyLarge,
                        text = "Resumen",
                        color = if(showResumen) Color.Black else MaterialTheme.colorScheme.primary
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
                        sumaParticipantes = sumaParticipantes,
                        onParticipanteClick = {}
                    )
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
                                key(gasto.idGasto){
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
        }
        CustomFloatButton(
            onNavNuevoGasto =  { onNavNuevoGasto(it) } ,
            onNavBalance = { onNavBalance(it) },
            modifier = Modifier.align(Alignment.BottomCenter), // Alinear el botón en la esquina inferior derecha
            showBalance = {
                showBalance = false
                showResumen = false
            },
            hojaDeGastos
        )
    }
}

@Composable
fun DatosHoja(hojaDeGastos: HojaConParticipantes?){
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
            Icon(
                modifier = Modifier
                    .size(50.dp),
                tint = MaterialTheme.colorScheme.error,
                imageVector = Icons.Default.Handshake, //IMAGEN DEL GASTO
                contentDescription = "Cierre de la Hoja",
            )
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
                        text = NumberFormat.getCurrencyInstance().format(gasto.importe.toInt()),
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
    onClick: () -> Unit) {

    val currencyFormatter = NumberFormat.getCurrencyInstance()
    Surface(
        shape = RoundedCornerShape(4.dp),
        elevation = 3.dp,
        color = MaterialTheme.colorScheme.onSecondary,
        modifier = Modifier
            .padding(vertical = 16.dp, horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Column( modifier = Modifier
            .padding(28.dp)
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = nombre,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = currencyFormatter.format(sumaGastos),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun ResumenBalanceDesing(
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
fun Resumen(
    balanceDeuda: Map<String, Double>?,
    sumaParticipantes: Map<String, Double>?,
    onParticipanteClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 7.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Spacer(modifier = Modifier.size(10.dp))
            Text(text = "Total gastado:")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                sumaParticipantes?.let{
                    items(sumaParticipantes.toList(), key = { it.first }) { participante ->
                        SumaPorParticipanteDesing(
                            nombre = participante.first,
                            sumaGastos = participante.second,
                            onClick = { onParticipanteClick(participante.first)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.size(20.dp))
            Text(text = "Resumen de deuda:")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                balanceDeuda?.let {
                    items(balanceDeuda.toList(), key = { it.first }) { (nombre, monto) ->
                        ResumenBalanceDesing(
                            participante = nombre,
                            monto = monto,
                            paddVert = 16
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomFloatButton(
    onNavNuevoGasto: (Long) -> Unit,
    onNavBalance: (Long) -> Unit,
    modifier: Modifier = Modifier,
    showBalance: () -> Unit,
    hojaDeGastos: HojaConParticipantes?
) {
    val imagenVector = if(hojaDeGastos?.hoja?.status == "C") Icons.Filled.ShoppingCart  else  Icons.Filled.Balance
    val navegarTo = {
        hojaDeGastos?.hoja?.idHoja?.let {
            if(hojaDeGastos.hoja.status == "C") onNavNuevoGasto(it) else onNavBalance(it)
        }
    }

    FloatingActionButton(
        onClick = {
            showBalance()
            navegarTo()
        },
        elevation = FloatingActionButtonDefaults.elevation(13.dp),
        modifier = modifier
            .height(120.dp)
            .width(80.dp)
            .padding(bottom = 54.dp, end = 14.dp), // Añade el padding al botón flotante
        shape = MaterialTheme.shapes.large,
        containerColor = if(hojaDeGastos?.hoja?.status == "C") MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            modifier = Modifier
                .size(50.dp),
            imageVector = imagenVector, //IMAGEN DEL GASTO
            contentDescription = "Pago o Balance",
        )
    }

}
/*************************/


//@Preview
//@Composable
//fun Preview(){
//    val innerPadding = PaddingValues()
//    val onNavNuevoGasto: (Int) -> Unit = {}
//    GastosScreen( innerPadding, {onNavNuevoGasto(3)})
//}
