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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.IconoGastoProvider
import com.app.miscuentas.data.model.IconoGasto
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Validaciones.Companion.isValid
import java.text.NumberFormat


@Composable
fun NuevoGasto(
    innerPadding: PaddingValues,
    idHojaPrincipal: Long?,
    onNavSplash: () -> Unit,
    navigateUp: () -> Unit,
    viewModel: NuevoGastoViewModel = hiltViewModel()
) {
    //Oculta Teclado
    val controlTeclado = LocalSoftwareKeyboardController.current

    val nuevoGastoState by viewModel.nuevoGastoState.collectAsState()
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()
    var showDialog by remember { mutableStateOf(false) }
    var mensaje by rememberSaveable { mutableStateOf("") }

    //paso el id de la hoja para registrar el gasto sobre esta misma.
    LaunchedEffect(idHojaPrincipal) {
        viewModel.onIdHojaPrincipalChanged(idHojaPrincipal)
    }
    LaunchedEffect(nuevoGastoState.cierreSesion) {
        when { (nuevoGastoState.cierreSesion) -> {
            viewModel.cerrarSesion()
            onNavSplash()
        } }
    }
    LaunchedEffect(nuevoGastoState.insertOk) {
        when { (nuevoGastoState.insertOk) -> navigateUp() }
    }
    LaunchedEffect(nuevoGastoState.superaLimite) {
        when {
            (nuevoGastoState.superaLimite) -> {
                mensaje = "Este gasto susperará el límite establecido para esta hoja."
                showDialog = true
                viewModel.onSuperaLimiteChanged(false)
            }
        }
    }

    if (showDialog) {
        MiAviso(
            true,
            mensaje,
            { showDialog = false }
        )
    }

    //Comprobar si tiene importe
    val onBotonGuardarClick = {
        when {
            nuevoGastoState.importe.isNotEmpty() -> {
                viewModel.insertaGasto()
            }
            else -> {
                mensaje = "No has indicado el IMPORTE."
                showDialog = true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color(color = 0xFFF5EFEF))
            .pointerInput(Unit) { //Oculta el teclado al colocar el foco en la caja
                detectTapGestures(onPress = {
                    controlTeclado?.hide()
                    awaitRelease()
                })
            }
    ) {
        NuevoGastoContent(
            onBotonGuardarClick,
            nuevoGastoState.importe,
            nuevoGastoState.hojaActual,
            nuevoGastoState.idPagador,
            nuevoGastoState.concepto,
            listaIconosGastos,
            { viewModel.onImporteTextFieldChanged(it) },
            { viewModel.onIdGastoFieldChanged(it) },
            { viewModel.onConceptoTextFieldChanged(it) },
            { viewModel.onPagadorChosen(it) }
        )
    }
}


@Composable
fun NuevoGastoContent(
    onBotonGuardarClick: () -> Unit,
    importe: String,
    hojaActual: HojaConParticipantes?,
    idPagador: Long,
    concepto: String,
    listaIconosGastos: List<IconoGasto>,
    onImporteTextFieldChanged: (String) -> Unit,
    onIdGastoFieldChanged: (Long) -> Unit,
    onConceptoTextFieldChanged: (String) -> Unit,
    onPagadorChosen: (ParticipanteConGastos) -> Unit
){
    val currencyFormatter = NumberFormat.getCurrencyInstance()
    var limite = "sin limite"
    if(!hojaActual?.hoja?.limite.isNullOrEmpty()){
        limite = currencyFormatter.format(hojaActual?.hoja?.limite?.toDouble())
    }

    LazyColumn (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        item {
            /** DATOS HOJA **/
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 15.dp, horizontal = 20.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                item {
                    Text(
                        text = hojaActual?.hoja?.titulo ?: "Buscando...",
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                item {
                    Row(
                        modifier = Modifier.padding(horizontal = 5.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "Limite: ",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Text(
                            text = limite,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }

            /** CONCEPTO **/
            ConceptoDesing(
                concepto,
                onConceptoTextFieldChanged,
                listaIconosGastos,
                onIdGastoFieldChanged
            )

            /** IMPORTE **/
            ImporteDesing(
                importe,
                onImporteTextFieldChanged
            )

            /** ELECCION PAGADOR **/
            PagadorDesing(
                hojaActual,
                idPagador,
                onPagadorChosen
            )

            CustomFloatButton({ onBotonGuardarClick() })
        }
    }
}

/** CONCEPTO **/
@Composable
fun ConceptoDesing(
    concepto: String,
    onConceptoTextFieldChanged: (String) -> Unit,
    listaIconosGastos: List<IconoGasto>,
    onIdGastoFieldChanged: (Long) -> Unit
){
    Column {
        Surface(
            shape = RoundedCornerShape(10.dp),
            elevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp, start = 15.dp, end = 15.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)

            ) {
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
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Concepto:",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    CustomTextfiel(
                        placeholder = "Varios",
                        value = concepto,
                        onTextFieldChange = { onConceptoTextFieldChanged(it) },
                        padding = 30
                    )
                }
            }
        }
    }
}


/** IMPORTE **/
@Composable
fun ImporteDesing(
    importe: String,
    onImporteTextFieldChanged: (String) -> Unit
){
    Surface(
        shape = RoundedCornerShape(10.dp),
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp, start = 15.dp, end = 15.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp, horizontal = 18.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Importe:",
                style = MaterialTheme.typography.bodyLarge
            )
            CustomTextfiel(
                placeholder = "0",
                value = importe,
                onTextFieldChange = { newValue ->
                    //Marca o desmarca el check:
                    if (newValue == "") {
                        onImporteTextFieldChanged(newValue)
                    } else if (isValid(newValue, 2)) {
                        onImporteTextFieldChanged(newValue)
                    }
                },
                padding = 70
            )
        }
    }
}

/** ELECCION PAGADOR **/
@Composable
fun PagadorDesing(
    hojaActual: HojaConParticipantes?,
    idPagador: Long,
    onPagadorChosen: (ParticipanteConGastos) -> Unit
){

    Surface(
        shape = RoundedCornerShape(10.dp),
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 8.dp, start = 15.dp, end = 15.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 7.dp, horizontal = 18.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pagador:",
                style = MaterialTheme.typography.bodyLarge
            )
            LazyRow {
                if (hojaActual?.participantes != null) {
                    itemsIndexed(hojaActual.participantes) { index, pagadorToList ->

                        CustomRadioButton(
                            pagadorIndex = index,
                            idPagadorState = idPagador,
                            pagador = pagadorToList,
                            onPagadorChosen =  { onPagadorChosen(it) }
                        )
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
    onTextFieldChange: (String) -> Unit,
    padding: Int
){
    TextField(
        modifier = Modifier
            .padding(horizontal = padding.dp),
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
            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
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
            .padding(
                start = 10.dp,
                bottom = 10.dp,
                end = 5.dp,
                top = if (!isSelected) 5.dp else 0.dp
            )
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


@Composable
fun CustomFloatButton(
    onBotonGuardarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = {
            onBotonGuardarClick()
        },
        elevation = FloatingActionButtonDefaults.elevation(13.dp),
        modifier = modifier
            .padding(15.dp)
            .width(160.dp)
            .height(60.dp),
        shape = MaterialTheme.shapes.large,
        containerColor =  MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Text(
            text = "AGREGAR",
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
        Spacer(modifier = Modifier.fillMaxHeight(0.10F))
    }
}
//@Preview
//@Composable
//fun Preview(){
//    val navController = rememberNavController()
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentScreen = backStackEntry?.destination?.route ?: SPLASH_ROUTE
//
//    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
//    val canNavigateBack = navBackStackEntry != null // Determinar si se puede navegar hacia atrás
//    NuevoGasto(null,  {navController.navigateUp()})
//}