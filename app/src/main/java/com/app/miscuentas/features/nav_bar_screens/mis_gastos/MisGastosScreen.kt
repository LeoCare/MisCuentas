package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.domain.model.Participante

@Composable
fun MisGastosScreen(
    innerPadding: PaddingValues?,
    viewModel: MisGastosViewModel = hiltViewModel()
){
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()
    val gastosState by viewModel.misGastosState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        SeleccionFiltros(
            onFilterChanged = {},
            onSortChanged = {},
            onDescendingChanged = {},
            listaIconosGastos,
            gastosState.hojasDelRegistrado
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(Color.DarkGray),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = "Todos",
                color = Color.White
            )
        }
        LazyColumn(
            contentPadding = innerPadding!!,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gastosState.gastos?.let {
                items(it) { gasto ->
                    val icono = listaIconosGastos.firstOrNull{ it.id.toLong() == gasto.tipo }
                    GastosDesing(gasto, icono)
                }
            }
        }
    }
}


@Composable
fun GastosDesing(
    gasto: DbGastosEntity,
    icono: IconoGasto?
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        elevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icono!!.imagen),
                contentDescription = "Icono del gasto",
                modifier = Modifier
                    .size(55.dp)
                    .background(Color.Gray, shape = CircleShape)
                    .padding(1.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = gasto.concepto, style = MaterialTheme.typography.labelLarge)
                Text(text = "Importe: ${gasto.importe}€", style = MaterialTheme.typography.labelLarge)
                Text(text = "Fecha Gasto: ${gasto.fechaGasto}", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
fun SeleccionFiltros(
    onFilterChanged: (String) -> Unit,
    onSortChanged: (String) -> Unit,
    onDescendingChanged: (Boolean) -> Unit,
    listaIconosGastos: List<IconoGasto>,
    listaHojas: List<HojaConParticipantes>
) {
    var filterSelected by rememberSaveable { mutableStateOf("Todos") }
    var sortSelected by rememberSaveable { mutableStateOf("Tipo") }
    var isDescending by rememberSaveable { mutableStateOf(false) }
    var isFilterExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(20.dp)) {
        Spacer(modifier = Modifier.height(20.dp))

        /** FILTRO **/
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Filtro:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    isFilterExpanded = if (isFilterExpanded){ false } else true
                    filterSelected = "Tipo"
                    onFilterChanged(filterSelected)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filterSelected == "Tipo") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
            ){
                Text(
                    text = "Tipo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (filterSelected == "Tipo") Color.White else Color.Black
                )
            }
            Button(
                onClick = {
                    isFilterExpanded = if (isFilterExpanded){ false } else true
                    filterSelected = "Hoja"
                    onFilterChanged(filterSelected)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filterSelected == "Hoja") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
            ){
                Text(
                    text = "Hoja",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (filterSelected == "Hoja") Color.White else Color.Black
                )
            }
            Button(
                onClick = {
                    isFilterExpanded = false
                    filterSelected = "Todos"
                    onFilterChanged(filterSelected)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filterSelected == "Todos") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
            ){
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (filterSelected == "Todos") Color.White else Color.Black
                )
            }

        }
        //Expandible para los filtros
        AnimatedVisibility(
            visible = isFilterExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .padding(16.dp)
            ) {
                if(filterSelected == "Tipo") FiltroTipos(listaIconosGastos)
                else if(filterSelected == "Hoja") FiltroHojas(listaHojas)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /** ORDEN **/
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Orden:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = {
                    sortSelected = "Tipo"
                    onSortChanged(sortSelected)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (sortSelected == "Tipo") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp),
                content = {
                    Text(
                        text = "Tipo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (sortSelected == "Tipo") Color.White else Color.Black
                    )
                }
            )
            Button(
                onClick = {
                    sortSelected = "Importe"
                    onSortChanged(sortSelected)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (sortSelected == "Importe") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp),
                content = {
                    Text(
                        text = "Importe",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (sortSelected == "Importe") Color.White else Color.Black
                    )
                }
            )

            Button(
                onClick = {
                    sortSelected = "Fecha"
                    onSortChanged(sortSelected)
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (sortSelected == "Fecha") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp),
                content = {
                    Text(
                        text = "Fecha",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (sortSelected == "Fecha") Color.White else Color.Black
                    )
                }
            )
            }
        }
        Spacer(modifier = Modifier.height(15.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Descendente",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Switch(
                checked = isDescending,
                onCheckedChange = {
                    isDescending = it
                    onDescendingChanged(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = Color.Gray
                )
            )
        }
    }
}

@Composable
fun FiltroTipos(
    listaIconosGastos: List<IconoGasto>
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        //Pintamos imagenes
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(listaIconosGastos) { icono ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(end = 15.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
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
//                                onConceptoTextFieldChanged(icono.nombre)
//                                onIdGastoFieldChanged(icono.id.toLong())
                                }
                        )
                    }
                    Text(
                        text = icono.nombre
                    )
                }
            }
        }
    }
}

@Composable
fun FiltroHojas(
    listaHojas: List<HojaConParticipantes>
){
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        //Pintamos las hojas
        LazyRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(listaHojas) { hoja ->
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.extraLarge)
                        .background(Color.DarkGray)
                        .padding(horizontal = 15.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = hoja.hoja.titulo,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White,
                        modifier = Modifier
                            .clickable {
//                                onConceptoTextFieldChanged(icono.nombre)
//                                onIdGastoFieldChanged(icono.id.toLong())
                            }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun FilterSortSectionPreview() {
    SeleccionFiltros(
        onFilterChanged = { },
        onSortChanged = { },
        onDescendingChanged = { },
        listaIconosGastos = listOf(),
        listaHojas = listOf()
    )
}