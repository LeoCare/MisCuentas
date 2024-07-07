package com.app.miscuentas.features.nuevo_gasto

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.FabPosition
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.util.Validaciones.Companion.isValid
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.features.splash.SPLASH_ROUTE
import com.app.miscuentas.util.Desing.Companion.MiAviso


@Composable
fun NuevoGasto(
    idHojaPrincipal: Long?,
    navigateUp: () -> Unit,
    viewModel: NuevoGastoViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val nuevoGastoState by viewModel.nuevoGastoState.collectAsState()
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()
    var showDialog by remember { mutableStateOf(false) } //valor mutable para el dialogo

    //paso el id de la hoja para registrar el gasto sobre esta misma.
    LaunchedEffect(idHojaPrincipal) {
        viewModel.onIdHojaPrincipalChanged(idHojaPrincipal)
    }

    LaunchedEffect(nuevoGastoState.insertOk) {
        when {
            (nuevoGastoState.insertOk) -> navigateUp()
        }
    }

    if (showDialog) {
        MiAviso(
            true,
            "No has indicado el IMPORTE.",
            { showDialog = false }
        )
    }

    //Comprobar si tiene importe
    val onBotonGuardarClick = {
        when {
            nuevoGastoState.importe.isNotEmpty() -> {
                //METODO PARA GUARDAR EL GASTO
                viewModel.insertaGasto()
            }
            else -> showDialog = true
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            Button(
                onClick = { onBotonGuardarClick() },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .padding(15.dp)
                    .width(160.dp)
                    .height(60.dp),
            ) {
                Text(
                    text = "AGREGAR",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.10F))
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding -> NuevoGastoContent(
            innerPadding,
            nuevoGastoState,
            listaIconosGastos,
            { viewModel.onImporteTextFieldChanged(it) },
            { viewModel.onIdGastoFieldChanged(it) },
            { viewModel.onConceptoTextFieldChanged(it) },
            { viewModel.onPagadorChosen(it) }
        )}
    )
}

@Composable
fun NuevoGastoContent(
    innerPadding: PaddingValues,
    nuevoGastoState: NuevoGastoState,
    listaIconosGastos: List<IconoGasto>,
    onImporteTextFieldChanged: (String) -> Unit,
    onIdGastoFieldChanged: (Long) -> Unit,
    onConceptoTextFieldChanged: (String) -> Unit,
    onPagadorChosen: (ParticipanteConGastos) -> Unit
){
    //Oculta Teclado
    val controlTeclado = LocalSoftwareKeyboardController.current

    LazyColumn(
        contentPadding = innerPadding,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .pointerInput(Unit) { //Oculta el teclado al colocar el foco en la caja
                detectTapGestures(onPress = {
                    controlTeclado?.hide()
                    awaitRelease()
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            /**  TITULO **/
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = nuevoGastoState.hojaActual?.hoja?.titulo ?: "Buscando...",
                    style = MaterialTheme.typography.titleLarge,
                )
            }
            /** IMPORTE **/
            Card(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.outline,
                    contentColor = Color.Black
                )
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 80.dp, vertical = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Importe",
                        style = MaterialTheme.typography.titleLarge
                    )
                    CustomTextfiel(
                        placeholder = "0",
                        value = nuevoGastoState.importe,
                        onTextFieldChange = { newValue ->
                            //Marca o desmarca el check:
                            if (newValue == "") {
                                onImporteTextFieldChanged(newValue)
                            } else if (isValid(newValue, 2)) {
                                onImporteTextFieldChanged(newValue)
                            }
                        }
                    )
                }
            }
        }
        item{

            /** ELECCION PAGADOR **/
            Card(
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large),

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.outline,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)

                ) {
                    Text(
                        text = "Pagador",
                        style = MaterialTheme.typography.titleLarge
                    )
                    LazyRow {
                        if (nuevoGastoState.hojaActual?.participantes != null) {
                            itemsIndexed(nuevoGastoState.hojaActual.participantes) { index, pagadorToList ->

                                CustomRadioButton(
                                    pagadorIndex = index,
                                    idPagadorState = nuevoGastoState.idPagador,
                                    pagador = pagadorToList,
                                    onPagadorChosen =  { onPagadorChosen(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
        item {

            Column {

                /** CONCEPTO/IMAGEN **/
                Card(
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, top = 10.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large),

                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.outline,
                        contentColor = Color.Black
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)

                    ) {
                        Text(
                            text = "Concepto",
                            style = MaterialTheme.typography.titleLarge
                        )
                        CustomTextfiel(
                            placeholder = "Varios",
                            value = nuevoGastoState.concepto,
                            onTextFieldChange = { onConceptoTextFieldChanged(it) }
                        )

                        //Pintamos imagenes
                        // Iconos de gastos en filas desplazables horizontalmente
                        listaIconosGastos.chunked(4).forEach { filaIconos ->
                            LazyRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                items(filaIconos) { icono ->
                                    Box(
                                        modifier = Modifier
                                            .clip(MaterialTheme.shapes.small)
                                    ) {
                                        Image(
                                            painter = painterResource(id = icono.imagen),
                                            contentDescription = "imagen gasto",
                                            modifier = Modifier
                                                .width(55.dp)
                                                .height(55.dp)
                                                .padding(bottom = 1.dp)
                                                .clickable {
                                                    onConceptoTextFieldChanged(icono.nombre)
                                                    onIdGastoFieldChanged(icono.id.toLong())
                                                }
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

@Composable
fun CustomTextfiel(
    placeholder: String,
    value: String,
    onTextFieldChange: (String) -> Unit
){
    TextField(
        modifier = Modifier
            .padding(horizontal = 40.dp),
        value = value,
        onValueChange = { onTextFieldChange(it) },
        placeholder = { Text( text = placeholder) },
        textStyle = MaterialTheme.typography.titleLarge,
        keyboardOptions = when (placeholder) {
            "0" -> KeyboardOptions(keyboardType = KeyboardType.Number)
            else -> KeyboardOptions(keyboardType = KeyboardType.Text)
        },
        singleLine = true,
        maxLines = 1,
        colors = TextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            focusedContainerColor = Color(0xFFD5E8F7),
            unfocusedContainerColor =  Color(0xFFF4F6F8)
        )
    )

}

/** Composable para la eleccion del participante pagador **/
@Composable
fun CustomRadioButton(
    pagadorIndex: Int,
    idPagadorState: Long,
    pagador: ParticipanteConGastos?,
    onPagadorChosen: (ParticipanteConGastos) -> Unit
){
    var isSelected = pagador?.participante?.idParticipante == idPagadorState
    if (idPagadorState.toInt() == 0 && pagadorIndex == 0) {
        isSelected = true
        onPagadorChosen(pagador!!)
    }

    val interactionSource =
        remember { MutableInteractionSource() } //Quito el efecto de sombra al clickar

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null//Quito el efecto de sombra al clickar

            ) { onPagadorChosen(pagador!!) }
            .padding(bottom = 10.dp, end = 15.dp, top = if (!isSelected) 10.dp else 0.dp)
    ) {

        RadioButton(
            selected = isSelected,
            onClick = { onPagadorChosen(pagador!!) },
            modifier = Modifier.padding(end = 7.dp)
        )
        AnimatedContent(
            targetState = isSelected,
            transitionSpec = {
                ContentTransform(fadeIn(tween(2000)), fadeOut(animationSpec = tween(100)))
            }, label = ""
        ) { selected ->
            Text(
                text = pagador!!.participante.nombre,
                style = if (selected) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
                color = if (selected) Color.Blue else Color.Black
            )
        }
    }

}

@Preview
@Composable
fun Preview(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = backStackEntry?.destination?.route ?: SPLASH_ROUTE

    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
    val canNavigateBack = navBackStackEntry != null // Determinar si se puede navegar hacia atrás
    NuevoGasto(null,  {navController.navigateUp()})
}