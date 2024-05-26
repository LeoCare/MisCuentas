@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.miscuentas.features.nueva_hoja

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import com.app.miscuentas.R
import com.app.miscuentas.util.Desing.Companion.showDatePickerDialog
import com.app.miscuentas.domain.Validaciones.Companion.isValid
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.navegacion.MisCuentasScreen
import com.app.miscuentas.util.Desing.Companion.MiAviso


//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
//@Preview
//@Composable
//fun Prev(){
//    val navController = rememberNavController()
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentScreen = MisCuentasScreen.valueOf(
//        backStackEntry?.destination?.route ?: MisCuentasScreen.MisHojas.name
//    )
//    val onNavSplash: () -> Unit = {}
//    val onNavMisHojas: () -> Unit = {}
//    val onNavNuevaHoja: () -> Unit = {}
//    NuevaHoja(
//        currentScreen,
//        onNavMisHojas,
//    )
//}

/** Composable principal de la Screen **/

@Composable
fun NuevaHoja(
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    onNavMisHojas: () -> Unit,
    viewModel: NuevaHojaViewModel = hiltViewModel()
){

    val eventoState by viewModel.nuevaHojaState.collectAsState()
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(eventoState.insertOk){
        if (eventoState.insertOk) {
            onNavMisHojas()
            viewModel.onInsertOkFieldChanged(false)
        }
    }


    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MiTopBar(
                context,
                null,
                "NUEVA HOJA",
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = canNavigateBack,
                navigateUp = { navigateUp() }
            )
        },

        content = { innerPadding -> NuevaHojaScreen(
            innerPadding,
            eventoState,
            { viewModel.onTituloFieldChanged(it)},
            { viewModel.onParticipanteFieldChanged(it) },
            { viewModel.onLimiteGastoFieldChanged(it) },
            { viewModel.onFechaCierreFieldChanged(it) },
            { viewModel.addParticipante(it) },
            { viewModel.insertAllHojaCalculo() },
            { viewModel.getTotalParticipantes() },
            { viewModel.deleteUltimoParticipante() }
        )}
    )
}

/** Contenedor del resto de elementos para la Screen**/
@Composable
fun NuevaHojaScreen(
    innerPadding: PaddingValues,
    eventoState: NuevaHojaState,
    onTituloFieldChanged: (String) -> Unit,
    onParticipanteFieldChanged: (String) -> Unit,
    onLimiteGastoFieldChanged: (String) -> Unit,
    onFechaCierreFieldChanged: (String) -> Unit,
    addParticipante: (Participante) -> Unit,
    insertAllHojaCalculo: () -> Unit,
    getTotalParticipantes: () -> Int,
    deleteUltimoParticipante: () -> Unit
) {

    val tieneLimite = remember { mutableStateOf(true) }
    val tieneFecha = remember{ mutableStateOf(true) }

    //Tipo letra
    val robotoMedItalic = FontFamily(Font(R.font.roboto_mediumitalic))

    //Oculta Teclado
    val controlTeclado = LocalSoftwareKeyboardController.current

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
            .pointerInput(Unit) { //Oculta el teclado al colocar el foco en la caja
                detectTapGestures(onPress = {
                    controlTeclado?.hide()
                    awaitRelease()
                })
            }
    ) {

        item {
            CustomSpacer(size = 10.dp)

            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxHeight(0.25f),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)

                ) {
                    Titulo(
                        value = eventoState.titulo
                    ) { onTituloFieldChanged(it) }
                }
            }

            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clip(MaterialTheme.shapes.large)
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Participantes(
                        eventoState.listaParticipantes,
                        eventoState.participante,
                        { onParticipanteFieldChanged(it) },
                        { addParticipante(it) },
                        { getTotalParticipantes() },
                        { deleteUltimoParticipante() }
                    )

                }
            }
        }

        item{
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxHeight(0.35f),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    LimiteGasto(
                        robotoMedItalic,
                        tieneLimite,
                        eventoState.limiteGasto) { onLimiteGastoFieldChanged(it) }

                    LimiteFecha(
                        eventoState.fechaCierre,
                        tieneFecha,
                        robotoMedItalic) { onFechaCierreFieldChanged(it) }

                }
            }

            CustomSpacer(20.dp)

            BotonCrear(
                eventoState.titulo,
                eventoState.listaParticipantes,
                { insertAllHojaCalculo() }
            )
        }
    }
}

/** Composable para el recuadro de Titulo **/
@Composable
fun Titulo(
    value: String,
    onTituloFieldChange: (String) -> Unit
) {

    var isFocused by remember { mutableStateOf(false) }

    Text(
        text = stringResource(R.string.titulo),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(start = 10.dp)
    )
    TextField(
        value = value,
        onValueChange = { onTituloFieldChange(it) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .height(IntrinsicSize.Min)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        textStyle = MaterialTheme.typography.titleLarge,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = Color(0xFFD5E8F7),
            unfocusedContainerColor =  Color(0xFFF4F6F8)
        ),
        singleLine = true,
        maxLines = 1
    )
}

/** Composable para el recuadro de Paraticipantes **/
@Composable
fun Participantes(
    listParticipantes: List<Participante>,
    statusParticipante: String,
    onParticipanteFieldChange: (String) -> Unit,
    addParticipante: (Participante) -> Unit,
    getTotalParticipantes: () -> Int,
    deleteUltimoParticipante: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    var mostrarParticipantes by remember { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(
            text = "Participantes",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier
                .align(CenterVertically)
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = getTotalParticipantes().toString(),
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            modifier = Modifier
                .align(CenterVertically)
        )

        Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Ver participantes",
            tint = Color.Black,
            modifier = Modifier
                .align(CenterVertically)

        )

    }

    Row(
        modifier = Modifier.padding(10.dp)
    ) {
        TextField(
            value = statusParticipante,
            onValueChange = { onParticipanteFieldChange(it) },
            modifier = Modifier
                .width(220.dp)
                .height(IntrinsicSize.Min),
            textStyle = MaterialTheme.typography.titleLarge,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = Color(0xFFD5E8F7),
                unfocusedContainerColor =  Color(0xFFF4F6F8)
            ),
            singleLine = true,
            maxLines = 1
        )
        Spacer(Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.icon_add),
            contentDescription = "Icono de agregar Participantes",
            modifier = Modifier
                .align(CenterVertically)
                .clickable {
                    if (statusParticipante.isNotBlank()) {
                        addParticipante(Participante(id = 0, nombre = statusParticipante))
                        onParticipanteFieldChange("")
                    }
                }
        )
        Spacer(Modifier.weight(1f))
        Image(
            painter = painterResource(id = R.drawable.icon_rest),
            contentDescription = "Icono de quitar Participantes",
            modifier = Modifier
                .align(CenterVertically)
                .clickable {
                    if (listParticipantes.isNotEmpty()) {
                        deleteUltimoParticipante()
                    }
                }
        )
    }
    Column {
        IconoVerParticipantes(
            mostrarParticipantes) { mostrarParticipantes = !mostrarParticipantes }

        ListaParticipantes(
            mostrarParticipantes,
            listParticipantes)
    }
}

/** Composable que genera y mantiene la lista de participantes **/
@Composable
fun ListaParticipantes(
    mostrarParticipantes: Boolean,
    listParticipantes: List<Participante>){

    if (mostrarParticipantes) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //mostrar lista de participantes
            items(listParticipantes) { participante ->
                Text(
                    text = participante.nombre,
                    modifier = Modifier
                        .padding(start = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

/** Composable para el recuadro de LimiteGasto **/
@Composable
fun LimiteGasto(
    robotoMedItalic: FontFamily,
    tieneLimite: MutableState<Boolean>,
    statusLimiteGasto: String,
    onLimiteGastoFieldChange: (String) -> Unit) {

    var isFocused by remember{ mutableStateOf(false) }

    Text(
        text = "Limite de gastos",
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 10.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically
    ) {
        TextField(
            value = statusLimiteGasto,
            onValueChange = { newValue ->
                //Marca o desmarca el check:
                if (newValue == "") {
                    onLimiteGastoFieldChange(newValue)
                    tieneLimite.value = true
                }
                else if (isValid(newValue, 2)) {
                    onLimiteGastoFieldChange(newValue)
                    tieneLimite.value = false
                }
            },
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .width(120.dp)
                .height(IntrinsicSize.Min),
            textStyle = MaterialTheme.typography.titleLarge,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = Color(0xFFD5E8F7),
                unfocusedContainerColor =  Color(0xFFF4F6F8)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            maxLines = 1
        )
        Image(
            painterResource(id = R.drawable.icon_euro),
            contentDescription = "Euro",
            modifier = Modifier
                .padding(start = 10.dp)
                .size(30.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.weight(1f))
            Checkbox(
                modifier = Modifier.align(Bottom),
                checked = tieneLimite.value,
                onCheckedChange = {
                    //Poner un limite al desmarcar el check:
                    tieneLimite.value = it
                    if(!tieneLimite.value && statusLimiteGasto == ""){
                        onLimiteGastoFieldChange("500")
                    }
                    else if (tieneLimite.value) onLimiteGastoFieldChange("")
                }
            )
            Text(
                text = "Sin Límite\n(sobra el dinero)",
                fontSize = 13.sp,
                fontFamily = robotoMedItalic
            )
        }
    }
}

/** Composable para el recuadro de LimiteFecha **/
@Composable
fun LimiteFecha(
    fechaCierre: String,
    tieneFecha: MutableState<Boolean>,
    robotoMedItalic: FontFamily,
    onFechaCierreFieldChanged: (String) -> Unit) {

    val context = LocalContext.current

    Text(
        text = "Fecha cierre",
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 10.dp, top = 10.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextField(
            value = fechaCierre,
            onValueChange = { if (fechaCierre.isEmpty()) tieneFecha.value = true },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .width(160.dp)
                .height(IntrinsicSize.Min),
            enabled = false,
            textStyle = MaterialTheme.typography.titleLarge,
            colors = TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedContainerColor = Color(0xFFD5E8F7),
                unfocusedContainerColor =  Color(0xFFF4F6F8)
            ),
            singleLine = true,
            maxLines = 1
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
           // Spacer(Modifier.weight(1f))
            Checkbox(
                checked = tieneFecha.value,
                onCheckedChange = {
                    tieneFecha.value = it
                    if(!tieneFecha.value) showDatePickerDialog(context, onFechaCierreFieldChanged)
                    else
                    {
                        onFechaCierreFieldChanged("")
                        tieneFecha.value = true
                    }
                }
            )
            Text(
                text = "Sin fecha\n(yo decido cuando)",
                fontSize = 13.sp,
                fontFamily = robotoMedItalic
            )
        }
    }
}

/** Boton de expandir participantes **/
@Composable
fun IconoVerParticipantes(
    expanded: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            imageVector = if(expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore ,
            contentDescription = "Ver participantes",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}


/** Composable para el boton de creacion de nueva hoja **/
@Composable
fun BotonCrear(
    titulo: String,
    listaParticipantes: List<Participante>,
    insertAllHojaCalculo: () -> Unit
) {

    var showDialog by remember { mutableStateOf(false) } //valor mutable para el dialogo

    //Prueba para mostrar los participantes almacenados en la BBDD //Borrar!!
    //val nombreDeTodos = getListaParticipatesStateString() //Borrar!!
    if (showDialog) {
        MiAviso(
            true,
            "Como minimo debe contener Titulo y un participante.",
            { showDialog = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (titulo.isEmpty() || listaParticipantes.isEmpty()) showDialog = true
                else { insertAllHojaCalculo() }
            },
            modifier = Modifier
                .height(60.dp)
                .width(180.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "CREAR",
                fontSize = 20.sp,
                color = Color.White
            )
        }
    }
}

/** ESPACIADOR  **/
@Composable
fun CustomSpacer(size: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
    )
}


