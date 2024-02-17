package com.app.miscuentas.features.nuevo_gasto

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.navegacion.MisCuentasScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoGasto(
    currentScreen: MisCuentasScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    viewModel: NuevoGastoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val nuevoGastoState by viewModel.nuevoGastoState.collectAsState()
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            MiTopBar(
                context,
                null,
                currentScreen,
                scope = scope,
                scaffoldState = scaffoldState,
                canNavigateBack = canNavigateBack,
                navigateUp = { navigateUp() }
            )
        },
        floatingActionButton = {
            Button(
                onClick = { /*TODO*/ },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier
                    .padding(15.dp)
                    .width(160.dp)
                    .height(60.dp),
            ) {
                Text(
                    text = "Agregar",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.14F))
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

    LazyColumn(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            /** IMPORTE **/
            Card(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 10.dp)
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
                        style = MaterialTheme.typography.titleMedium
                    )
                    CustomTextfiel(
                        placeholder = "0",
                        value = nuevoGastoState.importe,
                        onTextFieldChange = { onImporteTextFieldChanged(it) }
                    )
                }

            }

            /** ELECCION PAGADOR **/
            Card(
                modifier = Modifier
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
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
                        style = MaterialTheme.typography.titleMedium
                    )
                    CustomRadioButton(
                        pagadorState = nuevoGastoState.pagadorElegido,
                        onPagadorRadioChanged = { onPagadorRadioChanged(it) }
                    )
                }
            }

            /** CONCEPTO/IMAGEN **/
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, top = 5.dp)
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
                        style = MaterialTheme.typography.titleMedium
                    )
                    CustomTextfiel(
                        placeholder = "Varios",
                        value = nuevoGastoState.concepto,
                        onTextFieldChange = { onConceptoTextFieldChanged(it) }
                    )
                }
            }
        }
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 10.dp, bottom = 5.dp)
                    .clip(MaterialTheme.shapes.large),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = Color.Black
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 15.dp, end = 15.dp, bottom = 15.dp),
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
                                Image(
                                    painter = painterResource(id = icono.imagen),
                                    contentDescription = "imagen gasto",
                                    modifier = Modifier
                                        .width(55.dp)
                                        .height(55.dp)
                                        .padding(bottom = 1.dp)
                                )
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
    var isFocused by rememberSaveable { mutableStateOf(false) }

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
    pagadorState: Boolean,
    onPagadorRadioChanged: (Boolean) -> Unit){

    Row {
        RadioButton(
            selected = pagadorState,
            onClick = { onPagadorRadioChanged(false) },
            modifier = Modifier
                .padding(bottom = 7.dp)
        )
        Text(
            text = "participante",
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(bottom = 7.dp)
        )
    }
}

@Preview
@Composable
fun Prevew(){
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = MisCuentasScreen.valueOf(
        backStackEntry?.destination?.route ?: MisCuentasScreen.Splash.name
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState() //observar pila de navegacion
    val canNavigateBack = navBackStackEntry != null // Determinar si se puede navegar hacia atrás
    NuevoGasto(currentScreen, canNavigateBack, {navController.navigateUp()})
}