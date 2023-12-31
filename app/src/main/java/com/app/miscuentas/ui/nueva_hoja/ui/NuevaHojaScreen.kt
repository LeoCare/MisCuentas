package com.app.miscuentas.ui.nueva_hoja.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.R
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

    //CAMBIAR ESTO, ES SOLO PARA PRUEBA!!!
    var limiteGastos = remember { mutableStateOf("") }
    var fechaCierre = remember { mutableStateOf("") }
    var tieneLimite = remember { mutableStateOf(true) }
    var tieneFecha = remember { mutableStateOf(true) }
    val _participantes = remember { mutableStateListOf<String>("LEO","ANA","SAMY","ANA","SAMY","ANA","SAMY","ANA","SAMY") }
    val _titulo = remember { mutableStateOf("") }
    val _numParticipantes = remember { mutableStateOf("") }

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
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxHeight(0.25f),

            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Titulo(_titulo, robotoBlack)
                }
            }

            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .clip(MaterialTheme.shapes.small)
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
                    Paraticipantes(_numParticipantes, _participantes, robotoBlack)

                }
            }
        }

        item{
            Card(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxHeight(0.35f)
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    LimiteGasto(limiteGastos, tieneLimite, robotoMedItalic)

                    LimiteFecha(fechaCierre, tieneFecha, robotoMedItalic)

                }
            }

            CustomSpacer(20.dp)

            BotonCrear()
        }
    }
}


@Composable
fun Titulo(titulo: MutableState<String>, robotoBlack: FontFamily) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
    Text(
        text = stringResource(R.string.titulo),
        fontSize = 20.sp,
        color = Color.Black,
        fontFamily = robotoBlack, // Ajusta según tu fuente
        modifier = Modifier.padding(start = 10.dp)
    )
    TextField(
        value = titulo.value,
        onValueChange = { titulo.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .height(IntrinsicSize.Min),
        textStyle = TextStyle(
            fontSize = 17.sp,
            textAlign = TextAlign.Start,
            color = colorResource(id = R.color.purple_500)
        ),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFDFECF7)
        ),
        singleLine = true,
        maxLines = 1
    )
}


@Composable
fun Paraticipantes(_numParticipantes: MutableState<String>, _participantes: SnapshotStateList<String>, robotoBlack: FontFamily) {
    var isFocused by rememberSaveable { mutableStateOf(false) }
    var mostrarParticipantes by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp),
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
            text = "0",
            fontSize = 15.sp,
            color = Color.Black,
            fontFamily = robotoBlack,
            modifier = Modifier
                .align(CenterVertically)
        )
        IconButton(onClick = { mostrarParticipantes = !mostrarParticipantes }) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Ver participantes",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(CenterVertically)

            )
        }
    }

    Row(
        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
    ) {
        TextField(
            value = _numParticipantes.value,
            onValueChange = { _numParticipantes.value = it },
            modifier = Modifier
                .width(220.dp)
                .height(IntrinsicSize.Min),
            textStyle = TextStyle(
                fontSize = 17.sp,
                color = colorResource(id = R.color.purple_500)
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFDFECF7)
            ),
            singleLine = true,
            maxLines = 1
        )
        Spacer(modifier = Modifier.width(30.dp))
        Image(
            painter = painterResource(id = R.drawable.icon_add),
            contentDescription = "Icono de agregar Participantes",
            modifier = Modifier
                .align(CenterVertically)
                .clickable {
                    if (_numParticipantes.value.isNotBlank()) {
                        _participantes.add(_numParticipantes.value)
                        _numParticipantes.value = ""
                    }
                }
        )
        Spacer(modifier = Modifier.width(20.dp))
        Image(
            painter = painterResource(id = R.drawable.icon_rest),
            contentDescription = "Icono de agregar Participantes",
            modifier = Modifier
                .align(CenterVertically)
                .clickable {
                    if (!_participantes.isEmpty()) {
                        _participantes.remove(_participantes.last())
                    }
                }
        )
    }
    ListaParticipantes(mostrarParticipantes, _participantes)
}

@Composable
fun ListaParticipantes(mostrarParticipantes: Boolean, _participantes: SnapshotStateList<String>){
    if (mostrarParticipantes) {
        LazyRow(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
        ) {
            //mostrar lista de participantes
            items(_participantes) { participante ->
                Text(
                    text = participante,
                    modifier = Modifier
                        .padding(start = 10.dp),
                    color = colorResource(id = R.color.purple_500)
                )
            }
        }

    }
}

@Composable
fun LimiteGasto(
    limiteGastos: MutableState<String>,
    tieneLimite: MutableState<Boolean>,
    robotoMedItalic: FontFamily
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
            value = limiteGastos.value,
            onValueChange = { limiteGastos.value = it },
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .width(100.dp)
                .height(IntrinsicSize.Min),
            textStyle = TextStyle(
                fontSize = 17.sp,
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.purple_500)
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFDFECF7)
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            maxLines = 1
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.width(10.dp))
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
        verticalAlignment = CenterVertically
    ) {
        TextField(
            value = fechaCierre.value,
            onValueChange = { fechaCierre.value = it },
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .width(200.dp)
                .height(IntrinsicSize.Min),
            textStyle = TextStyle(
                fontSize = 17.sp,
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.purple_500)
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFDFECF7)
            ),
            singleLine = true,
            maxLines = 1
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.width(10.dp))
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


