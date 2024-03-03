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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import com.app.miscuentas.data.model.Hoja
import kotlin.random.Random


//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
@Preview
@Composable
fun Prev() {
    val padding = PaddingValues(20.dp)
    HojasScreen(padding)
}

/** Contenedor del resto de elementos para la pestaña Hojas **/
@Composable
fun HojasScreen(
    innerPadding: PaddingValues,
    viewModel: HojasViewModel = hiltViewModel()
) {

    // Screen con las hojas creadas
    //Provisional!!!
    val itemsTipo = listOf("Activas", "Finalizadas", "Todas")
    val itemsOrden = listOf("Fecha creacion", "Gasto total")

    val hojaState by viewModel.hojasState.collectAsState()

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
                SpinnerCustoms("MOSTRAR:", itemsTipo, "Filtrar por tipo")
                SpinnerCustoms("ORDENAR POR:", itemsOrden, "Opcion de ordenacion")
            }

            LazyColumn(
                contentPadding = innerPadding,
            ) {
                items(hojaState.listaHojas) { hoja ->
                    HojaDesing(hoja = hoja)
                }
            }
        }
    }

}




/** Composable para las opciones de filtrado **/
@Composable
fun SpinnerCustoms(titulo: String, items: List<String>, contentDescription: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    var selectedIndex by rememberSaveable { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {

        Text(text = titulo)

        Card(
            modifier = Modifier
                .padding(top = 10.dp)
                .height(IntrinsicSize.Min)
                .clip(MaterialTheme.shapes.extraLarge)
                .clickable { expanded = !expanded },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = Color.Black
            )
        ) {

            Row(modifier = Modifier.padding(6.dp))
            {
                Text(text = AnnotatedString(items[selectedIndex]))
                Icon(
                    imageVector = Icons.Filled.ExpandMore,
                    contentDescription = contentDescription,
                    tint = MaterialTheme.colorScheme.primary
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    items.forEachIndexed { index, item ->

                        DropdownMenuItem(onClick = {
                            selectedIndex = index
                            expanded = false
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
fun HojaDesing(hoja: Hoja) {
    var isChecked by rememberSaveable { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(start = 25.dp, end = 25.dp, bottom = 20.dp)
            .clip(MaterialTheme.shapes.extraLarge),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = Color.Black)
    ) {
        Column(
            modifier = Modifier
                .padding(start=20.dp, end=20.dp,bottom=10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(checked = isChecked, onCheckedChange = {isChecked = it})
                Text(
                    text = "Principal",
                )
                Spacer(Modifier.weight(1f))
//                Text(text = hoja.id)
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
                    //Text(text = hoja.type)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        Text(text = "Tipo:")
                        Text(text = hoja.type)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        Text(text = "Precio:")
                        Text(text = hoja.price.toString())
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
                    {
                        Text(text = "Id:")
                        Text(text = hoja.id)
                    }
                }
            }
            Text(
                text = "Resumen",
                modifier = Modifier.padding(start = 10.dp)
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


fun generateRandomColor(): Color {
    val random = Random
    val red = random.nextInt(0, 256)
    val green = random.nextInt(0, 256)
    val blue = random.nextInt(0, 256)
    return Color(red, green, blue)
}