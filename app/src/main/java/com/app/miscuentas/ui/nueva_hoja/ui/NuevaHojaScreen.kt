package com.app.miscuentas.ui.nueva_hoja.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
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

    //CAMBIAR ESTO
    var limiteGastos = remember { mutableStateOf("") }
    var fechaCierre = remember { mutableStateOf("") }
    var tieneLimite = remember { mutableStateOf(true) }
    var tieneFecha = remember { mutableStateOf(true) }
    val _participantes = remember { mutableStateListOf<String>("LEO","ANA","SAMY") }
    val _titulo = remember { mutableStateOf("") }
    val _numParticipantes = remember { mutableStateOf("") }

    val robotoBold = FontFamily(Font(R.font.roboto_bold))
    val robotoMedItalic = FontFamily(Font(R.font.roboto_mediumitalic))

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(color = 0xFFF5EFEF)) // Usa el color definido en los recursos
            .padding(top = 20.dp)
    ) {

        item {

            Titulo(_titulo, robotoBold)

            Paraticipantes(_numParticipantes, robotoBold)

        }

        //mostrar lista de participantes
        items(_participantes) { participante ->
            Text(
                text = participante,
                modifier = Modifier
                    .padding(start = 10.dp),
                color = Color(0xFF254204)
            )
        }

        item{

            LimiteGasto(limiteGastos, tieneLimite, robotoMedItalic)

            LimiteFecha(fechaCierre, tieneFecha, robotoMedItalic)

            CustomSpacer(30.dp)

            BotonCrear(_numParticipantes, _participantes)
        }
    }
}


@Composable
fun Titulo(titulo: MutableState<String>, robotoBold: FontFamily) {
    Text(
        text = stringResource(R.string.titulo),
        fontSize = 25.sp,
        color = Color.Black,
        fontFamily = robotoBold, // Ajusta según tu fuente
        modifier = Modifier.padding(start = 10.dp)
    )
    TextField(
        value = titulo.value,
        onValueChange = { titulo.value = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .height(50.dp),
        textStyle = TextStyle(
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            color = colorResource(id = R.color.purple_500)
        ),
        singleLine = true,
        maxLines = 1
    )
}


@Composable
fun Paraticipantes(_numParticipantes: MutableState<String>, robotoBold: FontFamily) {
    Row(
        modifier = Modifier.padding(start = 10.dp, top = 10.dp)
    ) {
        Text(
            text = "PARTICIPANTES",
            fontSize = 25.sp,
            color = Color.Black,
            fontFamily = robotoBold
        )
        Spacer(modifier = Modifier.width(10.dp))
//                Image(
//                    painter = painterResource(id = R.drawable.logologin),
//                    contentDescription = "Icono Participantes"
//                )
    }
    TextField(
        value = _numParticipantes.value,
        onValueChange = { _numParticipantes.value = it },
        modifier = Modifier
            .padding(start = 10.dp, top = 10.dp)
            .width(220.dp)
            .height(50.dp),
        textStyle = TextStyle(
            fontSize = 15.sp,
            textAlign = TextAlign.Start,
            color = colorResource(id = R.color.purple_500)
        ),
        singleLine = true,
        maxLines = 1
    )
}


@Composable
fun LimiteGasto(
    limiteGastos: MutableState<String>,
    tieneLimite: MutableState<Boolean>,
    robotoMedItalic: FontFamily
) {
    Text(
        text = "LIMITE DE GASTOS",
        fontSize = 25.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 10.dp, top = 20.dp)
//                fontFamily = FontFamily.Roboto
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = limiteGastos.value,
            onValueChange = { limiteGastos.value = it },
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .width(100.dp)
                .height(50.dp),
            textStyle = TextStyle(
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.purple_500)
            ),
            singleLine = true,
            maxLines = 1
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
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
                fontSize = 15.sp,
                fontFamily = robotoMedItalic
            )
        }
    }
}


@Composable
fun LimiteFecha(fechaCierre: MutableState<String>, tieneFecha: MutableState<Boolean>, robotoMedItalic: FontFamily) {
    Text(
        text = "FECHA CIERRE",
        fontSize = 25.sp,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 10.dp, top = 10.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = fechaCierre.value,
            onValueChange = { fechaCierre.value = it },
            modifier = Modifier
                .padding(start = 10.dp, top = 10.dp)
                .width(220.dp)
                .height(50.dp),
            textStyle = TextStyle(
                fontSize = 15.sp,
                textAlign = TextAlign.Start,
                color = colorResource(id = R.color.purple_500)
            ),
            singleLine = true,
            maxLines = 1
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.width(10.dp))
            Checkbox(
                checked = tieneFecha.value,
                onCheckedChange = { tieneFecha.value = it }
            )
            Text(
                text = "Sin fecha\n(yo decido cuando)",
                fontSize = 15.sp,
                fontFamily = robotoMedItalic
            )
        }
    }
}

@Composable
fun BotonCrear(_numParticipantes: MutableState<String>, _participantes: SnapshotStateList<String>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = {
                if (_numParticipantes.value.isNotBlank()) {
                    _participantes.add(_numParticipantes.value)
                    _numParticipantes.value = "" // Limpia el TextField después de agregar
                }
            },
            modifier = Modifier
                .height(70.dp)
                .width(200.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "CREAR",
                fontSize = 25.sp,
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


