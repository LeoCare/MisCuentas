package com.app.miscuentas.features.nav_bar_screens.participantes

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.data.model.IconoGasto
import com.app.miscuentas.data.model.Participante

/** Contenedor del resto de elementos para la pestaña Participantes **/
@Composable
fun ParticipantesScreen(
    innerPadding: PaddingValues?,
    viewModel: ParticipantesViewModel = hiltViewModel()
) {
    val participantesState by viewModel.participantesState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        /* FILTROS */
        SeleccionFiltros(
            filtroElegido = participantesState.filtroElegido,
            ordenElegido = participantesState.ordenElegido,
            descending = participantesState.descending,
            eleccionEnTitulo = participantesState.eleccionEnTitulo,
            onFiltroElegidoChanged = { viewModel.onFiltroElegidoChanged(it) },
            onOrdenElegidoChanged = { viewModel.onOrdenElegidoChanged(it) },
            onDescendingChanged = { viewModel.onDescendingChanged(it) },
            onFiltroHojaElegidoChanged = { viewModel.onFiltroHojaElegidoChanged(it) },
            onFiltroTipoElegidoChanged = { viewModel.onFiltroTipoElegidoChanged(it) },
            listaHojas = participantesState.hojasDelRegistrado,
            onEleccionEnTituloChanged = { viewModel.onEleccionEnTituloChanged(it) }
        )
        /* LISTA DE PARTICIPANTES */
        LazyColumn(
            contentPadding = innerPadding!!,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            participantesState.listaParticipantesAMostrar.let { listaParticipantesConGastos ->
                items(listaParticipantesConGastos, key = { it.participante.idParticipante }) { participanteConGastos ->
                    ParticipanteDesing(participanteConGastos, { viewModel.onParticipanteWithCorreoChanged(it) })
                }
            }
        }
    }
}

@Composable
fun ParticipanteDesing(
    participanteConGastos: ParticipanteConGastos,
    onParticipanteWithCorreoChanged: (DbParticipantesEntity) -> Unit
){
    var showDialog by remember { mutableStateOf(false) }
//    Surface(
//        shape = RoundedCornerShape(8.dp),
//        elevation = 12.dp,
//        modifier = Modifier
//            .padding(vertical = 3.dp, horizontal = 5.dp)
//            .fillMaxWidth()
//    ){
//        Row(
//            modifier = Modifier
//                .padding(8.dp)
//                .fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
////            Image(
////                painter = painterResource(id = icono!!.imagen),
////                contentDescription = "Icono del gasto",
////                modifier = Modifier
////                    .size(55.dp)
////                    .background(Color.Gray, shape = CircleShape)
////                    .padding(1.dp)
////            )
////            Spacer(modifier = Modifier.width(8.dp))
//            Column(
//                modifier = Modifier.fillMaxWidth(),
//                verticalArrangement = Arrangement.Center
//            ) {
//                Text(text = "Nombre: ${participanteConGastos.participante.nombre}", style = MaterialTheme.typography.labelLarge)
//                Text(text = "Correo: ${participanteConGastos.participante.correo ?: ""}", style = MaterialTheme.typography.labelLarge)
//                Text(text = "Tipo: ${participanteConGastos.participante.tipo}", style = MaterialTheme.typography.labelLarge)
//            }
//        }
        Surface(
            shape = RoundedCornerShape(18.dp),
            elevation = 9.dp,
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Nombre: ${participanteConGastos.participante.nombre}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    Text(
                        text = "Correo: ${participanteConGastos.participante.correo ?: ""}",
                        fontWeight = FontWeight.Bold
                    )
                    if (participanteConGastos.participante.correo == null){
                        Icon(
                            imageVector = Icons.Filled.Mail,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Icono correo a adjuntar",
                            modifier = Modifier
                                .align(CenterVertically)
                                .clickable {
                                    showDialog = true
                                }
                        )
                        CorreoElectronicoDialog(
                            showDialog = showDialog,
                            onDismiss = { showDialog = false },
                            onCorreoIntroducido = { correo ->
                                participanteConGastos.participante.correo = correo
                                onParticipanteWithCorreoChanged(participanteConGastos.participante)

                            }
                        )
                    }
                }
                Row {
                    Text(
                        text = "Tipo: ",
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 14.sp,
                    )
                    Text(
                        text = participanteConGastos.participante.tipo,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 14.sp,
                        color = if (participanteConGastos.participante.tipo == "ONLINE") MaterialTheme.colorScheme.onSecondaryContainer else Color.Black
                    )
                }
            }
        }

}

@Composable
fun CorreoElectronicoDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onCorreoIntroducido: (String) -> Unit
) {
    if (showDialog) {
        var correo by rememberSaveable { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Asignar correo al participante.")
            },
            text = {
                Column {
                    Text("Por favor, introduce un correo:")
                    TextField(
                        value = correo,
                        onValueChange = { correo = it },
                        placeholder = { Text(text = "ejemplo@correo.com") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onCorreoIntroducido(correo)
                    onDismiss()
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
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
    onFiltroTipoElegidoChanged: (String) -> Unit,
    listaHojas: List<HojaConParticipantes>,
    onEleccionEnTituloChanged: (String) -> Unit
) {
    var isFilterExpanded by rememberSaveable { mutableStateOf(false) }

    Column(modifier = Modifier.padding(vertical = 7.dp, horizontal = 27.dp)) {
        Spacer(modifier = Modifier.height(10.dp))

        /** FILTRO **/
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Filtro:",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodyMedium,
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
                    .height(if (filtroElegido == "Tipo") 37.dp else 32.dp)
            ){
                Text(
                    text = "Tipo",
                    style = MaterialTheme.typography.bodySmall,
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
                    .height(if (filtroElegido == "Hoja") 37.dp else 32.dp)
            ){
                Text(
                    text = "Hoja",
                    style = MaterialTheme.typography.bodySmall,
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
                    .height(if (filtroElegido == "Todos") 37.dp else 32.dp)
            ){
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.bodySmall,
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
                if(filtroElegido == "Tipo") FiltroTipos({onFiltroTipoElegidoChanged(it)}, { onEleccionEnTituloChanged(it)})
                else if(filtroElegido == "Hoja") FiltroHojas(listaHojas, {onFiltroHojaElegidoChanged(it)}, { onEleccionEnTituloChanged(it)})
            }
        }

        /** ORDEN **/
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Orden:",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodyMedium,
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
                        .height(if (ordenElegido == "Tipo") 37.dp else 32.dp),
                    content = {
                        Text(
                            text = "Tipo",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (ordenElegido == "Tipo") Color.White else Color.Black
                        )
                    }
                )
                Button(
                    onClick = {
                        onOrdenElegidoChanged("Nombre")
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (ordenElegido == "Nombre") MaterialTheme.colorScheme.primary else Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(if (ordenElegido == "Nombre") 37.dp else 32.dp),
                    content = {
                        Text(
                            text = "Nombre",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (ordenElegido == "Nombre") Color.White else Color.Black
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
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodySmall
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
            text = "Mostrar por $filtroElegido ${if (eleccionEnTitulo != "") ": $eleccionEnTitulo" else ""}",
            color = Color.White
        )
    }
}

@Composable
fun FiltroTipos(
    onFiltroTipoElegidoChanged: (String) -> Unit,
    onEleccionEnTituloChanged: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LOCAL",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onEleccionEnTituloChanged("LOCAL")
                        onFiltroTipoElegidoChanged("LOCAL")
                    }
            )
            Text(
                text = "ONLINE",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onEleccionEnTituloChanged("ONLINE")
                        onFiltroTipoElegidoChanged("ONLINE")
                    }
            )
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
