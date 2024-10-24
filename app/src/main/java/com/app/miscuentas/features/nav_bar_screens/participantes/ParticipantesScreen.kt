package com.app.miscuentas.features.nav_bar_screens.participantes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.data.local.dbroom.entitys.DbParticipantesEntity
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.local.dbroom.relaciones.ParticipanteConGastos
import com.app.miscuentas.util.Desing.Companion.CorreoElectronicoDialog
import com.app.miscuentas.util.Desing.Companion.MiAviso
import com.app.miscuentas.util.Validaciones.Companion.contrasennaOk
import com.app.miscuentas.util.Validaciones.Companion.emailCorrecto

/** Contenedor del resto de elementos para la pestaña Participantes **/
@Composable
fun ParticipantesScreen(
    innerPadding: PaddingValues?,
    idHojaAMostrar: Long,
    viewModel: ParticipantesViewModel = hiltViewModel()
) {
    val participantesState by viewModel.participantesState.collectAsState()

    //Hoja a mostrar pasada por el Screen Hojas
    LaunchedEffect(Unit) {
        viewModel.onNavHojaElegidoChanged(idHojaAMostrar)
        viewModel.getIdRegistroPreference()

    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        /* FILTROS */
        SeleccionFiltros(
            filtroElegido = participantesState.filtroElegido,
            ordenElegido = participantesState.ordenElegido,
            descending = participantesState.descending,
            eleccionEnTitulo = participantesState.eleccionEnTitulo,
            onFiltroElegidoChanged =  viewModel::onFiltroElegidoChanged,
            onOrdenElegidoChanged = viewModel::onOrdenElegidoChanged,
            onDescendingChanged = viewModel::onDescendingChanged,
            onFiltroHojaElegidoChanged = viewModel::onFiltroHojaElegidoChanged,
            onFiltroTipoElegidoChanged = viewModel::onFiltroTipoElegidoChanged,
            listaHojas = participantesState.hojasDelRegistrado,
            onEleccionEnTituloChanged = viewModel::onEleccionEnTituloChanged
        )
        /* LISTA DE PARTICIPANTES */
        LazyColumn(
            contentPadding = innerPadding!!,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            participantesState.listaParticipantesAMostrar.let { listaParticipantesConGastos ->
                items(listaParticipantesConGastos/*, key = { it.participante.idParticipante }*/) { participanteConGastos ->
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
    var showMessage by remember { mutableStateOf(false) }
    var titulo by rememberSaveable { mutableStateOf("") } //Titulo a mostrar
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar

    if (showMessage) {
        MiAviso(show = true,
            titulo = titulo,
            mensaje = mensaje,
            cerrar = { showMessage = false }
            )
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.onPrimary,//if (participanteConGastos.participante.idUsuarioParti != null) MaterialTheme.colorScheme.outlineVariant else MaterialTheme.colorScheme.onPrimary,
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
            Row {
                Text(
                    text = "Nombre: ",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = participanteConGastos.participante.nombre,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Row {
                Text(
                    text = "Correo: "
                )
                Text(
                    text = participanteConGastos.participante.correo ?: "",
                    fontWeight = FontWeight.Bold
                )



                if (participanteConGastos.participante.correo == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Invitar",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Filled.Mail,
                            tint = MaterialTheme.colorScheme.primary,
                            contentDescription = "Icono correo a adjuntar",
                            modifier = Modifier
                                .align(CenterVertically)
                                .clickable {
                                    titulo = "Asignar correo al participante."
                                    mensaje = "Por favor, introduce un correo:"
                                    showDialog = true
                                }
                        )
                        CorreoElectronicoDialog(
                            showDialog = showDialog,
                            onDismiss = { showDialog = false },
                            titulo = titulo,
                            mensaje = mensaje,
                            onCorreoIntroducido = { correo ->
                                if (!emailCorrecto(correo)) {
                                    titulo = "ERROR"
                                    mensaje = "La sintaxis del correo no es correcta!"
                                    showMessage = true
                                } else {
                                    participanteConGastos.participante.correo = correo
                                    onParticipanteWithCorreoChanged(participanteConGastos.participante)
                                }

                            }
                        )
                    }
                }
                else {
                    Row (
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()

                    ){
                        if(participanteConGastos.participante.idUsuarioParti != null){
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                contentDescription = "Correo confirmado",
                                modifier = Modifier
                                    .align(CenterVertically)
                                    .clickable {
                                        titulo = "AVISO"
                                        mensaje = "Correo confirmado!"
                                        showMessage = true
                                    }
                            )
                        }
                        else {
                            Icon(
                                imageVector = Icons.Default.CheckCircleOutline,
                                tint = MaterialTheme.colorScheme.primary,
                                contentDescription = "Correo aun sin confirmar",
                                modifier = Modifier
                                    .align(CenterVertically)
                                    .clickable {
                                        titulo = "AVISO"
                                        mensaje = "Correo aun sin confirmar"
                                        showMessage = true
                                    }
                            )
                        }
                    }

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
            verticalAlignment = CenterVertically) {
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
            verticalAlignment = CenterVertically
        ) {
            Text(
                text = "Orden:",
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(end = 10.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = CenterVertically) {
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
            verticalAlignment = CenterVertically,
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
            .height(20.dp)
            .background(MaterialTheme.colorScheme.outline),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "Mostrar por $filtroElegido ${if (eleccionEnTitulo != "") ": $eleccionEnTitulo" else ""}",
            color = Color.DarkGray
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
