package com.app.miscuentas.features.mis_hojas.nav_bar_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import com.app.miscuentas.domain.model.HojaCalculo
import kotlin.random.Random


//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
//@Preview
//@Composable
//fun Prev() {
//    val padding = PaddingValues(20.dp)
//    HojasScreen(padding)
//}

/** Contenedor del resto de elementos para la pestaña Hojas **/
@Composable
fun HojasScreen(
    innerPadding: PaddingValues?,
    onNavGastos: (Int) -> Unit,
    viewModel: HojasViewModel = hiltViewModel()
) {

    // Screen con las hojas creadas
    //Provisional!!!
    val itemsTipo = listOf("Activas", "Finalizadas", "Anuladas", "Todas")
    val itemsOrden = listOf("Fecha creacion", "Fecha cierre")

    val hojaState by viewModel.hojasState.collectAsState()


    LaunchedEffect(Unit) {
        viewModel.getAllHojasCalculos()
    }

    if (hojaState.circularIndicator){
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
    else {
        Column(horizontalAlignment = CenterHorizontally) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpinnerCustoms("Mostrar:", itemsTipo, "Filtrar por tipo"){ mostrar ->
                    when(mostrar) {
                        "Activas" -> {}
                        "Finalizadas" -> {}
                        "Anuladas" -> {}
                        "Todas" -> {}
                    }

                }
                SpinnerCustoms("Ordenar por:", itemsOrden, "Opcion de ordenacion"){ Orden ->
                    when(Orden) {
                        "Fecha creacion" -> { viewModel.ordenHoja() }
                        "Fecha cierre" -> { viewModel.ordenHojadesc() }
                    }
                }
            }

            LazyColumn(
                contentPadding = innerPadding!!,
            ) {
                if (hojaState.listaHojas != null){
                    itemsIndexed(hojaState.listaHojas!!){index, hojaCalculoToList ->
                        HojaDesing(
                            onNavGastos = {onNavGastos(it)},
                            index = index,
                            hojaCalculo = hojaCalculoToList
                        )
                    }
                }
            }
        }
    }
}




/** Composable para las opciones de filtrado **/
@Composable
fun SpinnerCustoms(
    titulo: String,
    items: List<String>,
    contentDescription: String,
    onOptionSelected: (String) -> Unit
){
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(20.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Text(text = titulo)

        Card(
            modifier = Modifier
                .height(IntrinsicSize.Min)
                .clip(MaterialTheme.shapes.extraSmall)
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(
                containerColor =  MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            )
        ) {

            Row(modifier = Modifier.padding(6.dp))
            {
                Text(text = AnnotatedString(items[selectedIndex]))
                Icon(
                    imageVector = if(expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = contentDescription,
                    tint = Color.White
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEachIndexed { index, item ->

                        DropdownMenuItem(onClick = {
                            selectedIndex = index
                            expanded = false
                            onOptionSelected( item)
                        }) {
                            Text(text = item)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun HojaDesing(
 /** API **/ //  hoja: Hoja
 onNavGastos: (Int) -> Unit,
 index: Int,
 hojaCalculo: HojaCalculo
) {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable { onNavGastos(hojaCalculo.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.outline,
            contentColor = Color.Black)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical =10.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = hojaCalculo.titulo,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                /** API **/ //   Text(text = hoja.type)
                when (hojaCalculo.status) { //pinta segun valor status de la BBDD
                    "C" ->
                        Text(
                            text = "Activa",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    "A" ->
                        Text(
                            text = "Anulada",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    else -> Text(
                        text = "Finalizada",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.hoja),
                        contentDescription = "Logo Hoja",
                        modifier = Modifier
                            .width(80.dp)
                            .height(80.dp)
                    )
                }
                Column{
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
                    {


                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        Text(
                            text = "Participantes:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = hojaCalculo.participantesHoja.size.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                        /** API **/ //   Text(text = hoja.price.toString())
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        Text(
                            text = "Fecha Cierre:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = hojaCalculo.fechaCierre ?: "sin definir",
                            style = MaterialTheme.typography.titleMedium
                        )
                        /** API **/ //  Text(text = hoja.id)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        Text(
                            text = "Limite:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = if(hojaCalculo.limite == null) "sin definir" else hojaCalculo.limite.toString(),
                            style = MaterialTheme.typography.titleMedium
                        )
                        /** API **/ //  Text(text = hoja.id)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        Text(
                            text = "Creada el:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = hojaCalculo.fechaCreacion!!,
                            style = MaterialTheme.typography.titleMedium
                        )
                        /** API **/ //   Text(text = hoja.type)
                    }

                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Resumen",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 10.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
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


fun generateRandomColor(): Color {
    val random = Random
    val red = random.nextInt(0, 256)
    val green = random.nextInt(0, 256)
    val blue = random.nextInt(0, 256)
    return Color(red, green, blue)
}