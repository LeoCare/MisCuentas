@file:OptIn(ExperimentalMaterial3Api::class)

package com.app.miscuentas.ui.nueva_hoja.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.R
import com.app.miscuentas.ui.Validaciones
import com.app.miscuentas.ui.Validaciones.Companion.isValid
import com.app.miscuentas.ui.navegacion.MiTopBar
import com.app.miscuentas.ui.navegacion.MisCuentasScreem

//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
@Preview
@Composable
fun Prev(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreem.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreem.Nueva_Hoja.name
    )

    NuevaHoja(
        currentScreen,
        navController,
        onNavMisHojas = { navController.navigate(MisCuentasScreem.Mis_Hojas.name) }
    )
}


@Composable
fun NuevaHoja(
    currentScreen: MisCuentasScreem,
    navController: NavHostController,
    onNavMisHojas: () -> Unit
){

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MiTopBar(
                currentScreen,
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = true,
                navigateUp = { navController.navigateUp() })},
        content = { innerPadding -> NuevaHojaScreen(innerPadding) }
    )
}


/** CONTENIDO GENERAL DE ESTA SCREEN **/

@Composable
fun NuevaHojaScreen(innerPadding: PaddingValues) {

    val viewModel: NuevaHojaViewModel = viewModel()
    //variables que delegan sus valores al cambio del viewModel
    val statusTitulo by viewModel.titulo.collectAsState()
    val statusParticipante by viewModel.participante.collectAsState()
    val statusLimiteGasto by viewModel.limiteGasto.collectAsState()
    val listParticipantes by viewModel.listaParticipantes.collectAsState() //lista de participantes

    //Provisional
    var fechaCierre = remember { mutableStateOf("") }
    var tieneLimite = remember { mutableStateOf(true) }
    var tieneFecha = remember { mutableStateOf(true) }

    //Tipo letra
    val robotoBlack = FontFamily(Font(R.font.roboto_black))
    val robotoMedItalic = FontFamily(Font(R.font.roboto_mediumitalic))

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(color = 0xFFF5EFEF))
            .padding(top = 10.dp)

    ) {

        item {
            Text(
                text = "CREAR NUEVA HOJA",
                fontSize = 25.sp,
                fontFamily = robotoBlack,
                modifier = Modifier.padding(start = 10.dp)
            )
            CustomSpacer(size = 20.dp)

            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxHeight(0.25f),

            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Titulo(robotoBlack, value = statusTitulo) { viewModel.onTituloFieldChanged(it) }
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
                    )
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Paraticipantes(robotoBlack, listParticipantes, statusParticipante) { viewModel.onParticipanteFieldChanged(it) }

                }
            }
        }

        item{
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .fillMaxHeight(0.35f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    LimiteGasto(robotoMedItalic, tieneLimite, statusLimiteGasto) { viewModel.onLimiteGastoFieldChanged(it) }

                    LimiteFecha(fechaCierre, tieneFecha, robotoMedItalic)

                }
            }

            CustomSpacer(20.dp)

            BotonCrear()
        }
    }
}


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
            .height(IntrinsicSize.Min),
        textStyle = TextStyle(
            fontSize = 17.sp,
            textAlign = TextAlign.Start,
            color = MaterialTheme.colorScheme.onTertiaryContainer
        ),
        colors = TextFieldDefaults.textFieldColors(
            containerColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFD3D7DA)
        ),
        singleLine = true,
        maxLines = 1
    )
}


@Composable
fun Paraticipantes(
    robotoBlack: FontFamily,
    listParticipantes: List<String>,
    statusParticipante: String,
    onParticipanteFieldChange: (String) -> Unit
    ) {
    val viewModel: NuevaHojaViewModel = viewModel()
    val isFocused by rememberSaveable { mutableStateOf(false) }
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
            tint = MaterialTheme.colorScheme.primary,
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
            textStyle = TextStyle(
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFD3D7DA)
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
                    if (!listParticipantes.isEmpty()) {
                        viewModel.deleteUltimoParticipante()
                    }
                }
        )
    }
    Column {
        IconButton(onClick = { mostrarParticipantes = !mostrarParticipantes }) {
            Icon(
                imageVector = if(mostrarParticipantes) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore ,
                contentDescription = "Ver participantes",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        ListaParticipantes(mostrarParticipantes, listParticipantes)
    }

}

@Composable
fun ListaParticipantes(mostrarParticipantes: Boolean, listParticipantes: List<String>){

    if (mostrarParticipantes) {
        LazyRow(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            //mostrar lista de participantes
            items(listParticipantes) { participante ->
                Text(
                    text = participante,
                    modifier = Modifier
                        .padding(start = 10.dp),
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }
    }
}

@Composable
fun LimiteGasto(
    robotoMedItalic: FontFamily,
    tieneLimite: MutableState<Boolean>,
    statusLimiteGasto: String,
    onLimiteGastoFieldChange: (String) -> Unit
) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
    Text(
        text = "Limite de gastos",
        fontSize = 20.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 10.dp)
//                fontFamily = FontFamily.Roboto
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = CenterVertically
    ) {
        TextField(
            value = statusLimiteGasto,
            onValueChange = { newValue ->
                if (isValid(newValue, 2)) {
                    onLimiteGastoFieldChange(newValue)
                }
            },
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .width(100.dp)
                .height(IntrinsicSize.Min),
            textStyle = TextStyle(
                fontSize = 17.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFD3D7DA)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            maxLines = 1
        )
        Image(
            painterResource(id = R.drawable.icon_euro),
            contentDescription = "Euro",
            modifier = Modifier
                .align(Bottom)
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
                modifier = Modifier.align(Alignment.Bottom),
                checked = tieneLimite.value,
                onCheckedChange = { tieneLimite.value = it }
            )
            Text(
                text = "Sin Límite\n(sobra el dinero)",
                fontSize = 13.sp,
                fontFamily = robotoMedItalic
            )
        }
    }
}


@Composable
fun LimiteFecha(fechaCierre: MutableState<String>, tieneFecha: MutableState<Boolean>, robotoMedItalic: FontFamily) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
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
            value = fechaCierre.value,
            onValueChange = { fechaCierre.value = it },
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .width(180.dp)
                .height(IntrinsicSize.Min),
            textStyle = TextStyle(
                fontSize = 17.sp,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            ),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFD3D7DA)
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
                onCheckedChange = { tieneFecha.value = it }
            )
            Text(
                text = "Sin fecha\n(yo decido cuando)",
                fontSize = 13.sp,
                fontFamily = robotoMedItalic
            )
        }
    }
}

//BOTON EXPANDIR PARTICIPANTES
@Composable
fun VerParticipantes(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        androidx.compose.material3.Icon(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = "Ver participantes"
        )
    }
}


//BOTON CREAR HOJA
@Composable
fun BotonCrear() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { },
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


//ESPACIADOR
@Composable
fun CustomSpacer(size: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(size)
    )
}


