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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.IconoGasto

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
            filtroElegido = gastosState.filtroElegido,
            ordenElegido = gastosState.ordenElegido,
            descending = gastosState.descending,
            eleccionEnTitulo = gastosState.eleccionEnTitulo,
            onFiltroElegidoChanged = { viewModel.onFiltroElegidoChanged(it)},
            onOrdenElegidoChanged = {viewModel.onOrdenElegidoChanged(it)},
            onDescendingChanged = { viewModel.onDescendingChanged(it)},
            onFiltroHojaElegidoChanged = {viewModel.onFiltroHojaElegidoChanged(it)},
            onFiltroTipoElegidoChanged = { viewModel.onFiltroTipoElegidoChanged(it)},
            listaIconosGastos = listaIconosGastos,
            listaHojas = gastosState.hojasDelRegistrado,
            onEleccionEnTituloChanged = { viewModel.onEleccionEnTituloChanged(it)}
        )
        Row(
            modifier = Modifier
                .padding( 15.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total: ${gastosState.sumaGastos}€",
                style = MaterialTheme.typography.titleMedium
            )
        }
        LazyColumn(
            contentPadding = innerPadding!!,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            gastosState.listaGastosAMostrar?.let { listaGasto ->
                items(listaGasto) { gasto ->
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
        elevation = 12.dp,
        modifier = Modifier
            .padding(vertical = 3.dp, horizontal = 5.dp)
            .fillMaxWidth()
    ){
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
    filtroElegido: String,
    ordenElegido: String,
    descending: Boolean,
    eleccionEnTitulo: String,
    onFiltroElegidoChanged: (String) -> Unit,
    onOrdenElegidoChanged: (String) -> Unit,
    onDescendingChanged: (Boolean) -> Unit,
    onFiltroHojaElegidoChanged: (Long) -> Unit,
    onFiltroTipoElegidoChanged: (Long) -> Unit,
    listaIconosGastos: List<IconoGasto>,
    listaHojas: List<HojaConParticipantes>,
    onEleccionEnTituloChanged: (String) -> Unit
) {
    var isFilterExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.padding(20.dp)) {
        Spacer(modifier = Modifier.height(10.dp))

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
                    isFilterExpanded =
                        if (isFilterExpanded){
                            when(filtroElegido){
                                "Tipo"-> false
                                else -> true
                            }
                        }  else true
                    onFiltroElegidoChanged("Tipo")
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filtroElegido == "Tipo") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
            ){
                Text(
                    text = "Tipo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (filtroElegido == "Tipo") Color.White else Color.Black
                )
            }
            Button(
                onClick = {
                    isFilterExpanded =
                        if (isFilterExpanded){
                            when(filtroElegido){
                                "Hoja"-> false
                                else -> true
                            }
                        }  else true
                    onFiltroElegidoChanged("Hoja")
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filtroElegido == "Hoja") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
            ){
                Text(
                    text = "Hoja",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (filtroElegido == "Hoja") Color.White else Color.Black
                )
            }
            Button(
                onClick = {
                    isFilterExpanded = false
                    onFiltroElegidoChanged("Todos")
                    onEleccionEnTituloChanged("")
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filtroElegido == "Todos") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp)
            ){
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (filtroElegido == "Todos") Color.White else Color.Black
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
                if(filtroElegido == "Tipo") FiltroTipos(listaIconosGastos, {onFiltroTipoElegidoChanged(it)}, { onEleccionEnTituloChanged(it)})
                else if(filtroElegido == "Hoja") FiltroHojas(listaHojas, {onFiltroHojaElegidoChanged(it)}, { onEleccionEnTituloChanged(it)})
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

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
                    onOrdenElegidoChanged("Tipo")
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (ordenElegido == "Tipo") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp),
                content = {
                    Text(
                        text = "Tipo",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (ordenElegido == "Tipo") Color.White else Color.Black
                    )
                }
            )
            Button(
                onClick = {
                    onOrdenElegidoChanged("Importe")
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (ordenElegido == "Importe") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp),
                content = {
                    Text(
                        text = "Importe",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (ordenElegido == "Importe") Color.White else Color.Black
                    )
                }
            )

            Button(
                onClick = {
                    onOrdenElegidoChanged("Fecha")
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (ordenElegido == "Fecha") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(35.dp),
                content = {
                    Text(
                        text = "Fecha",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (ordenElegido == "Fecha") Color.White else Color.Black
                    )
                }
            )
            }
        }
        Spacer(modifier = Modifier.height(5.dp))
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
                checked = descending,
                onCheckedChange = {onDescendingChanged(it)}
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "Mostrar $filtroElegido ${if (eleccionEnTitulo != "") ": $eleccionEnTitulo" else ""}",
            color = Color.White
        )
    }
}

@Composable
fun FiltroTipos(
    listaIconosGastos: List<IconoGasto>,
    onFiltroTipoElegidoChanged: (Long) -> Unit,
    onEleccionEnTituloChanged: (String) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth(),
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
                                    onEleccionEnTituloChanged(icono.nombre)
                                    onFiltroTipoElegidoChanged(icono.id.toLong())
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
    listaHojas: List<HojaConParticipantes>,
    onFiltroHojaElegidoChanged: (Long) -> Unit,
    onEleccionEnTituloChanged: (String) -> Unit
){
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
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
                                onEleccionEnTituloChanged(hoja.hoja.titulo)
                                onFiltroHojaElegidoChanged(hoja.hoja.idHoja)
                            }
                    )
                }
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun FilterSortSectionPreview() {
//    MisGastosScreen(
//        innerPadding = null,
//
//    )
//}