package com.app.miscuentas.features.nav_bar_screens.mis_gastos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.data.local.dbroom.entitys.DbGastosEntity
import com.app.miscuentas.domain.model.Participante
import com.app.miscuentas.features.nueva_hoja.IconoVerParticipantes
import com.app.miscuentas.features.nueva_hoja.ListaParticipantes

@Composable
fun MisGastosScreen(
    innerPadding: PaddingValues?,
    viewModel: MisGastosViewModel = hiltViewModel()
){
    val listParticipantes: List<Participante> = listOf()
    var mostrarParticipantes by remember { mutableStateOf(true) }
    val gastosState by viewModel.misGastosState.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        LazyColumn(contentPadding = innerPadding!!) {
            item{
                Row(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Card(
                        modifier = Modifier
                            .padding(start = 10.dp, end = 10.dp, bottom = 9.dp)
                            .clip(MaterialTheme.shapes.extraLarge),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.outline,
                            contentColor = Color.Black)
                    ) {
                        Text(
                            text = "Tipos de gastos",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                }
            }
            if(gastosState.participanteConGastos != null){
                itemsIndexed( gastosState.participanteConGastos!!.gastos) { _, gasto ->
                    GastosDesing(
                        gasto
                    )
                }
            }
            item{
            Column {
                IconoVerIconos(
                    mostrarParticipantes
                ) { mostrarParticipantes = !mostrarParticipantes }

                ListaIcon(
                    mostrarParticipantes,
                    listParticipantes)
            }
            }
        }
    }
}

@Composable
fun GastosDesing(
    gasto: DbGastosEntity
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = gasto.concepto
        )
        Text(
            text = gasto.importe
        )
    }
}

@Composable
fun ListaIcon(
    mostrarParticipantes: Boolean,
    lisIcon: List<Participante>){

    if (mostrarParticipantes) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            //mostrar lista de participantes
            items(lisIcon) { participante ->
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