package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.data.local.repository.IconoGastoProvider
import com.app.miscuentas.domain.model.IconoGasto
import com.app.miscuentas.domain.model.Participante

@Composable
fun MisGastosScreen(
    innerPadding: PaddingValues?,
    viewModel: MisGastosViewModel = hiltViewModel()
){
    val listParticipantes: List<Participante> = listOf()
    var mostrarParticipantes by remember { mutableStateOf(true) }
    val listaIconosGastos = IconoGastoProvider.getListIconoGasto()
    val gastosState by viewModel.misGastosState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ){
        FilterSortSection(
            onFilterChanged = {},
            onSortChanged = {},
            onDescendingChanged = {}
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            gastosState.participanteConGastos?.let {
                items(it.gastos) { gasto ->
                    val icono = listaIconosGastos[gasto.tipo.toInt()]
                    GastosDesing(gasto, icono)
                }
            }
        }
        //gastosState.participanteConGastos?.let { ListaGastos(it.gastos, listaIconosGastos) }
    }
}



@Composable
fun ListaGastos(
    gastos: List<DbGastosEntity>,
    listaIconosGastos: List<IconoGasto>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(gastos) { gasto ->
            val icono = listaIconosGastos[gasto.idGasto.toInt()]
            GastosDesing(gasto, icono)
        }
    }
}
@Composable
fun GastosDesing(
    gasto: DbGastosEntity,
    icono: IconoGasto
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
                painter = painterResource(id = icono.imagen),
                contentDescription = "Icono del gasto",
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.Gray, shape = CircleShape)
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = gasto.concepto, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Importe: ${gasto.importe}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Fecha Gasto: ${gasto.fechaGasto}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}

@Composable
fun ListaIcon(
    mostrarParticipantes: Boolean,
    lisIcon: List<Participante>
){
    if (mostrarParticipantes) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //mostrar lista de participantes
            itemsIndexed(lisIcon) {_,  participante ->
                Text(
                    text = participante.nombre,
                    modifier = Modifier
                        .padding(start = 10.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}
@Composable
fun IconoVerIconos(
    expanded: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
    ) {
        Icon(
            imageVector = if(expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore ,
            contentDescription = "Ver participantes",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun FilterSortSection(
    onFilterChanged: (String) -> Unit,
    onSortChanged: (String) -> Unit,
    onDescendingChanged: (Boolean) -> Unit
) {
    var filterSelected by rememberSaveable { mutableStateOf("Tipo") }
    var isFilterSelecd by rememberSaveable { mutableStateOf(false)}
    var sortSelected by rememberSaveable { mutableStateOf("Tipo") }
    var isDescending by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.padding(20.dp)) {
        Spacer(modifier = Modifier.height(20.dp))

        /** FILTRO **/
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Filtro:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
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

        }

        /** ORDEN **/
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Orden:",
                style = MaterialTheme.typography.bodyLarge,
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
                style = MaterialTheme.typography.bodyLarge
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

//@Preview(showBackground = true)
//@Composable
//fun PreviewListaGastos() {
//    val sampleGastos = remember {
//        listOf(
//            DbGastosEntity(1,1,"Supermercado", "120€", "01/12/2024", null),
//            DbGastosEntity(1,1,"Restaurante", "45€", "02/12/2024",null),
//            DbGastosEntity(1,1,"Transporte", "30€", "03/12/2024",null),
//            DbGastosEntity(1,1,"Entretenimiento", "60€", "04/12/2024",null)
//        )
//    }
//    ListaGastos(sampleGastos)
//}