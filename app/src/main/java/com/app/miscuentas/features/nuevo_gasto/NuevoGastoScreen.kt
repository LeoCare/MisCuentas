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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.Validaciones.Companion.isValid
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.navegacion.MisCuentasScreen


@Composable
fun NuevoGasto(
    idHojaPrincipal: Int?,
    currentScreen: String,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    viewModel: NuevoGastoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val nuevoGastoState by viewModel.nuevoGastoState.collectAsState()
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()

    LaunchedEffect(Unit) {
        viewModel.onIdHojaPrincipalChanged(idHojaPrincipal)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MiTopBar(
                context,
                null,
                "NUEVO GASTO",
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = canNavigateBack,
                navigateUp = { navigateUp() }
            )
        },
        floatingActionButton = {
            Button(
                onClick = { viewModel.insertAllHojaCalculoLinDet() },
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
            Spacer(modifier = Modifier.fillMaxHeight(0.13F))
        },
        floatingActionButtonPosition = FabPosition.Center,
        content = { innerPadding -> NuevoGastoContent(
            innerPadding,
            nuevoGastoState,
            listaIconosGastos,
            { viewModel.onImporteTextFieldChanged(it)},
            { viewModel.onConceptoTextFieldChanged(it)},
            { viewModel.onPagadorChosen(it) },
            { viewModel.onPagadorRadioChanged(it) }
        )}
    )
}

@Composable
fun NuevoGastoContent(
    innerPadding: PaddingValues,
    nuevoGastoState: NuevoGastoState,
    listaIconosGastos: List<IconoGasto>,
    onImporteTextFieldChanged: (String) -> Unit,
    onConceptoTextFieldChanged: (String) -> Unit,
    onPagadorChosen: (String) -> Unit,
    onPagadorRadioChanged: (Boolean) -> Unit
    ){

    //Oculta Teclado
    val controlTeclado = LocalSoftwareKeyboardController.current

    val pagadorSelected = remember { mutableStateOf("") }

     LazyColumn(
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
             /** IMPORTE **/
             Card(
                 modifier = Modifier
                     .padding(start = 15.dp, end = 15.dp, top = 10.dp)
                     .fillMaxWidth()
                     .clip(MaterialTheme.shapes.large),

                 colors = CardDefaults.cardColors(
                     containerColor = MaterialTheme.colorScheme.surfaceVariant,
                     contentColor = Color.Black
                 )
             ) {

                 Column(
                     modifier = Modifier
                         .fillMaxWidth()
                         .padding(horizontal = 100.dp, vertical = 15.dp),
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
                        .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.large),

                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = Color.Black
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)

                    ) {
                        Text(
                            text = "Pagador",
                            style = MaterialTheme.typography.titleLarge
                        )
                        LazyRow {
                            if (nuevoGastoState.hojaActual?.participantesHoja != null) {
                                itemsIndexed(nuevoGastoState.hojaActual.participantesHoja!!) { index, pagadorToList ->

                                    CustomRadioButton(
                                        pagadorIndex = index,
                                        pagadorSelected = pagadorSelected,
                                        pagadorToList = pagadorToList.nombre,
                                        onPagadorChosen =  { pagadorSelected.value = it }
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
                         .padding(start = 15.dp, end = 15.dp, top = 5.dp)
                         .fillMaxWidth()
                         .clip(MaterialTheme.shapes.large),

                     colors = CardDefaults.cardColors(
                         containerColor = MaterialTheme.colorScheme.surfaceVariant,
                         contentColor = Color.Black
                     )
                 ) {
                     Column(
                         modifier = Modifier
                             .fillMaxWidth()
                             .padding(15.dp),
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
                                                 .clickable { onConceptoTextFieldChanged(icono.nombre) }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextfiel(
    placeholder: String,
    value: String,
    onTextFieldChange: (String) -> Unit
){
    val isFocused by rememberSaveable { mutableStateOf(false) }

    TextField(
        modifier = Modifier
            .padding(horizontal = 40.dp),
        value = value,
        onValueChange = { onTextFieldChange(it) },
        placeholder = { Text( text = placeholder) },
        keyboardOptions = when (placeholder) {
            "0" -> KeyboardOptions(keyboardType = KeyboardType.Number)
            else -> KeyboardOptions(keyboardType = KeyboardType.Text)
        },
        singleLine = true,
        maxLines = 1,
        //textStyle = MaterialTheme.typography.labelMedium,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = if (isFocused) Color(0xFFD5E8F7) else Color(0xFFF4F6F8)
        )
    )

}

/** Composable para la eleccion del participante pagador **/
@Composable
fun CustomRadioButton(
    pagadorIndex: Int,
    pagadorSelected: MutableState<String>,
    pagadorToList: String,
    onPagadorChosen: (String) -> Unit
){
    var isSelected = pagadorToList == pagadorSelected.value
    if (pagadorSelected.value.isEmpty()) isSelected = pagadorIndex == 0

    val interactionSource = remember { MutableInteractionSource() } //Quito el efecto de sombra al clickar

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null//Quito el efecto de sombra al clickar

            ) { onPagadorChosen(pagadorToList) }
            .padding(bottom = 10.dp, end = 15.dp, top = if (!isSelected) 10.dp else 0.dp)
    ) {

        RadioButton(
            selected = isSelected,
            onClick = null,//{ onPagadorChosen(pagadorToList) },
            modifier = Modifier.padding(end = 7.dp)
        )
        AnimatedContent(
            targetState = isSelected,
            transitionSpec = {
                ContentTransform(fadeIn(tween(2000)), fadeOut(animationSpec = tween(100)))
            }, label = ""
        ) { selected ->
            Text(
                text = pagadorToList,
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
    val currentScreen = backStackEntry?.destination?.route ?: MisCuentasScreen.Splash.route

    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
    val canNavigateBack = navBackStackEntry != null // Determinar si se puede navegar hacia atrás
    NuevoGasto(null, currentScreen, canNavigateBack,  {navController.navigateUp()})
}