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
import androidx.compose.ui.ExperimentalComposeUiApi
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.R
import com.app.miscuentas.util.Desing.Companion.showDatePickerDialog
import com.app.miscuentas.util.MiDialogo
import com.app.miscuentas.domain.Validaciones.Companion.isValid
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.navegacion.MisCuentasScreen

//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
@Preview
@Composable
fun Prev(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreen.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreen.NuevaHoja.name
    )

    NuevaHoja(
        currentScreen,
        navController,
        onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.name) }
    )
}

/** Composable principal de la Screen **/
@Composable
fun NuevaHoja(
    currentScreen: MisCuentasScreen,
    navController: NavHostController,
    onNavMisHojas: () -> Unit
){
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
    val canNavigateBack = navBackStackEntry != null // Determinar si se puede navegar hacia atrás

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MiTopBar(
                null,
                currentScreen,
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = canNavigateBack
            ) { navController.navigateUp() }
        },
        content = { innerPadding -> NuevaHojaScreen(innerPadding) }
    )
}

/** Contenedor del resto de elementos para la Screen**/
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NuevaHojaScreen(innerPadding: PaddingValues) {

    // Observa el estado completo del evento
    val viewModel: NuevaHojaViewModel = hiltViewModel()
    val eventoState by viewModel.eventoState.collectAsState()

    //Provisional
    val tieneLimite = remember { mutableStateOf(true) }
    val tieneFecha = remember { mutableStateOf(true) }

    //Tipo letra
    val robotoBlack = FontFamily(Font(R.font.roboto_black))
    val robotoMedItalic = FontFamily(Font(R.font.roboto_mediumitalic))

    //Oculta Teclado
    val controlTeclado = LocalSoftwareKeyboardController.current

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .pointerInput(Unit) { //Oculta el teclado al colocar el foco en la caja
                detectTapGestures(onPress = {
                    controlTeclado?.hide()
                    awaitRelease()
                })
            }
    ) {

        item {
            Text(
                text = "CREAR NUEVA HOJA",
                fontSize = 25.sp,
                fontFamily = robotoBlack,
                modifier = Modifier.padding(start = 10.dp)
            )
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
                        robotoBlack,
                        value = eventoState.titulo
                    ) { viewModel.onTituloFieldChanged(it) }
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
                        viewModel,
                        robotoBlack,
                        eventoState.listaParticipantes,
                        eventoState.participante) { viewModel.onParticipanteFieldChanged(it) }

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
                        eventoState.limiteGasto) { viewModel.onLimiteGastoFieldChanged(it) }

                    LimiteFecha(
                        eventoState.fechaCierre,
                        tieneFecha,
                        robotoMedItalic) { viewModel.onFechaCierreFieldChanged(it) }

                }
            }

            CustomSpacer(20.dp)

            BotonCrear(viewModel)
        }
    }
}

/** Composable para el recuadro de Titulo **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Titulo(
    robotoBlack: FontFamily,
    value: String,
    onTituloFieldChange: (String) -> Unit) {

    var isFocused by rememberSaveable { mutableStateOf(false) }

    Text(
        text = stringResource(R.string.titulo),
        fontSize = 20.sp,
        color = Color.Black,
        fontFamily = robotoBlack, // Ajusta según tu fuente
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
        textStyle = TextStyle(
            fontSize = 17.sp,
            textAlign = TextAlign.Start
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFE7EBEE)
        ),
        singleLine = true,
        maxLines = 1
    )
}

/** Composable para el recuadro de Paraticipantes **/
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Participantes(
    viewModel: NuevaHojaViewModel,
    robotoBlack: FontFamily,
    listParticipantes: List<String>,
    statusParticipante: String,
    onParticipanteFieldChange: (String) -> Unit
    ) {
    //val viewModel: NuevaHojaViewModel = viewModel()
    var isFocused by rememberSaveable { mutableStateOf(false) }
    var mostrarParticipantes by rememberSaveable { mutableStateOf(true) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween

    ) {
        Text(
            text = "Participantes",
            fontSize = 20.sp,
            color = Color.Black,
            fontFamily = robotoBlack,
            modifier = Modifier
                .align(CenterVertically)
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = viewModel.getTotalParticipantes().toString(),
            fontSize = 15.sp,
            color = Color.Black,
            fontFamily = robotoBlack,
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
                .height(IntrinsicSize.Min)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            textStyle = TextStyle(
                fontSize = 17.sp
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFE7EBEE)
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
                        viewModel.addParticipante(statusParticipante)
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
                        viewModel.deleteUltimoParticipante()
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
    listParticipantes: List<String>){

    if (mostrarParticipantes) {
        LazyRow(
            modifier = Modifier
//                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            //mostrar lista de participantes
            items(listParticipantes) { participante ->
                Text(
                    text = participante,
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
    var isFocused by rememberSaveable { mutableStateOf(false) }
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
                .width(100.dp)
                .height(IntrinsicSize.Min)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            textStyle = TextStyle(
                fontSize = 17.sp,
                textAlign = TextAlign.Start
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFE7EBEE)
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
@OptIn(ExperimentalMaterial3Api::class)
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
            onValueChange = {  },
            modifier = Modifier
                .padding(horizontal = 10.dp, vertical = 10.dp)
                .width(180.dp)
                .height(IntrinsicSize.Min),
            enabled = false,
            textStyle = TextStyle(
                fontSize = 17.sp,
                textAlign = TextAlign.Start
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor =  Color(0xFFE7EBEE)
            ),
            singleLine = true,
            maxLines = 1
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(Modifier.weight(1f))
            Checkbox(
                checked = tieneFecha.value,
                onCheckedChange = {
                    tieneFecha.value = it
                    if(!tieneFecha.value) showDatePickerDialog(context, onFechaCierreFieldChanged)
                    else onFechaCierreFieldChanged("")
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
fun BotonCrear(viewModel: NuevaHojaViewModel) {

    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo

    //Prueba para mostrar los participantes almacenados en la BBDD //Borrar!!
    val nombreDeTodos = viewModel.getAllParticipantesToString() //Borrar!!
    if (showDialog) MiDialogo(nombreDeTodos, {showDialog =  false}, { showDialog = true}) //Borrar!!


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                viewModel.insertAllParticipante()
                showDialog = true
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


