package com.app.miscuentas.features.nav_bar_screens.mis_hojas

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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.data.model.HojaCalculo
import com.app.miscuentas.util.Desing
import com.app.miscuentas.util.Desing.Companion.MiDialogoWithOptions2
import kotlinx.coroutines.launch


/** Contenedor del resto de elementos para la pestaña Hojas **/
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MisHojasScreen(
    innerPadding: PaddingValues?,
    onNavGastos: (Long) -> Unit,
    onNavParticipantes: (Long) -> Unit,
    viewModel: MisHojasViewModel = hiltViewModel()
) {

    val coroutineScope = rememberCoroutineScope()
    val hojaState by viewModel.misHojasState.collectAsState()

    // Estado del SwipeRefresh
    val pullRefreshState  = rememberPullRefreshState(
        refreshing = hojaState.isRefreshing,
        onRefresh = {
            coroutineScope.launch {
                //METODO PARA REFRESCAR LOS DATOS
                viewModel.ActualizarDatos()
            }
        }
    )

    LaunchedEffect(hojaState.opcionSelected){
        when(hojaState.opcionSelected) {
            "Finalizar","Anular" -> { viewModel.updateStatusHoja() }
            "Eliminar" -> { viewModel.deleteHojaConParticipantes() }
            "S" -> { viewModel.updateUnirmeHoja(true) }
            "N" -> { viewModel.updateUnirmeHoja(false) }
            "Quitar" -> {viewModel.quitarHojaNPropi() }
        }
    }
    Column(
        modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
            .pullRefresh(pullRefreshState)
    ) {
        SeleccionFiltros(
            filtroElegido = hojaState.filtroElegido,
            ordenElegido = hojaState.ordenElegido,
            descending = hojaState.descending,
            eleccionEnTitulo = hojaState.eleccionEnTitulo,
            onTipoOrdenChanged = viewModel::onTipoOrdenChanged,
            onDescendingChanged = viewModel::onDescendingChanged,
            onFiltroElegidoChanged = viewModel::onFiltroElegidoChanged,
            onFiltroTipoElegidoChanged = viewModel::onFiltroTipoElegidoChanged,
            onFiltroEstadoElegidoChanged = viewModel::onFiltroEstadoElegidoChanged,
            onEleccionEnTituloChanged = viewModel::onEleccionEnTituloChanged
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Propietaria",
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                Icons.Default.Circle,
                contentDescription = "Menu de opciones",
                tint = MaterialTheme.colorScheme.tertiaryContainer
            )
            Text(
                text = "Invitado",
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                Icons.Default.Circle,
                contentDescription = "Menu de opciones",
                tint = MaterialTheme.colorScheme.inverseSurface
            )
            Text(
                text = "Sin confirmar",
                style = MaterialTheme.typography.bodyMedium
            )
            Icon(
                Icons.Default.Circle,
                contentDescription = "Menu de opciones",
                tint = MaterialTheme.colorScheme.scrim
            )
        }
        if(hojaState.isRefreshing){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Indicador de recarga
                PullRefreshIndicator(
                    refreshing = true,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        else {
            HojasList(
                innerPadding,
                hojaState.listaHojasAMostrar,
                hojaState.idRegistro,
                onNavGastos,
                onNavParticipantes,
                viewModel::onOpcionSelectedChanged,
                viewModel::onStatusChanged,
                viewModel::onHojaAModificarChanged
            )
        }
    }
}


@Composable
fun SeleccionFiltros(
    filtroElegido: String,
    ordenElegido: String,
    descending: Boolean,
    onFiltroElegidoChanged: (String) -> Unit,
    onFiltroEstadoElegidoChanged: (String) -> Unit,
    onFiltroTipoElegidoChanged: (String) -> Unit,
    eleccionEnTitulo: String,
    onTipoOrdenChanged: (String) -> Unit,
    onDescendingChanged: (Boolean) -> Unit,
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
                                "Estado"-> false
                                else -> true
                            }
                        }  else true
                    onFiltroElegidoChanged("Estado")
                },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filtroElegido == "Estado") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(if (filtroElegido == "Estado") 37.dp else 32.dp)
            ){
                Text(
                    text = "Estado",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (filtroElegido == "Estado") Color.White else Color.Black
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
                else if(filtroElegido == "Estado") FiltroEstados({onFiltroEstadoElegidoChanged(it)}, { onEleccionEnTituloChanged(it)})
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        onTipoOrdenChanged("Fecha Creacion")
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (ordenElegido == "Fecha Creacion") MaterialTheme.colorScheme.primary else Color.White
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(if (ordenElegido == "Fecha Creacion") 37.dp else 32.dp),
                    content = {
                        Text(
                            text = "Fecha Creacion",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (ordenElegido == "Fecha Creacion") Color.White else Color.Black
                        )
                    }
                )
                Button(
                    onClick = {
                        onTipoOrdenChanged("Fecha Cierre")
                    },
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (ordenElegido == "Fecha Cierre") MaterialTheme.colorScheme.primary else Color.White
                    ),
                    modifier =
                    Modifier
                        .weight(1f)
                        .height(if (ordenElegido == "Fecha Cierre") 37.dp else 32.dp),
                    content = {
                        Text(
                            text = "Fecha Cierre",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (ordenElegido == "Fecha Cierre") Color.White else Color.Black
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
                onCheckedChange = { onDescendingChanged(it) }
            )
        }
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp)
            .background(MaterialTheme.colorScheme.outline),
        verticalAlignment = Alignment.CenterVertically,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Circle,
                contentDescription = "Menu de opciones",
                tint = MaterialTheme.colorScheme.tertiaryContainer
            )
            Text(
                text = "Propietaria",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onEleccionEnTituloChanged("Propietaria")
                        onFiltroTipoElegidoChanged("Propietaria")
                    }
            )
            Icon(
                Icons.Default.Circle,
                contentDescription = "Menu de opciones",
                tint = MaterialTheme.colorScheme.inverseSurface
            )
            Text(
                text = "Invitado",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onEleccionEnTituloChanged("Invitado")
                        onFiltroTipoElegidoChanged("Invitado")
                    }
            )
            Icon(
                Icons.Default.Circle,
                contentDescription = "Menu de opciones",
                tint = MaterialTheme.colorScheme.scrim
            )
            Text(
                text = "Sin confirmar",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onEleccionEnTituloChanged("SinConfirmar")
                        onFiltroTipoElegidoChanged("SinConfirmar")
                    }
            )
        }
    }
}


@Composable
fun FiltroEstados(
    onFiltroEstadosElegidoChanged: (String) -> Unit,
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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Activas",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onEleccionEnTituloChanged("En Curso")
                        onFiltroEstadosElegidoChanged("C")
                    }
            )
            Text(
                text = "Finalizadas",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onEleccionEnTituloChanged("Finalizadas")
                        onFiltroEstadosElegidoChanged("F")
                    }
            )
            Text(
                text = "Anuladas",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                modifier = Modifier
                    .clickable {
                        onEleccionEnTituloChanged("Anuladas")
                        onFiltroEstadosElegidoChanged("A")
                    }
            )
        }
    }
}


@Composable
fun HojasList(
    innerPadding: PaddingValues?,
    listaHojasAMostrar: List<HojaConParticipantes>?,
    idRegistro: Long,
    onNavGastos: (Long) -> Unit,
    onNavParticipantes: (Long) -> Unit,
    onOpcionSelectedChanged: (String) -> Unit,
    onStatusChanged: (HojaCalculo, String) -> Unit,
    onHojaAModificarChanged: (HojaCalculo) -> Unit,
) {
    LazyColumn(contentPadding = innerPadding!!) {
        listaHojasAMostrar?.let { list ->
            itemsIndexed(list) { _, hojaConParticipantes ->
                HojaDesing(
                    idRegistro = idRegistro,
                    onNavGastos = { onNavGastos(it) },
                    onNavParticipantes = { onNavParticipantes(it) },
                    hojaConParticipantes = hojaConParticipantes,
                    onOpcionSelectedChanged = { onOpcionSelectedChanged(it) },
                    onStatusChanged = { onStatusChanged(hojaConParticipantes.hoja.toDomain(), it) },
                    onHojaAModificarChanged = { onHojaAModificarChanged(hojaConParticipantes.hoja.toDomain()) }
                )
            }
        }
    }
}


@Composable
fun HojaDesing(
    idRegistro: Long,
    onNavGastos: (Long) -> Unit,
    onNavParticipantes: (Long) -> Unit,
    hojaConParticipantes: HojaConParticipantes,
    onOpcionSelectedChanged: (String) -> Unit,
    onStatusChanged: (String) -> Unit,
    onHojaAModificarChanged: () -> Unit
) {

    /** Eleccion y cambio de estado:  **/
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showAviso by rememberSaveable { mutableStateOf(false) }
    var showOpciones by rememberSaveable { mutableStateOf(false) }
    var titulo by rememberSaveable { mutableStateOf("") }
    var mensaje by rememberSaveable { mutableStateOf("") }
    var opcionSeleccionada by rememberSaveable { mutableStateOf("") }
    var opcionAceptada by rememberSaveable { mutableStateOf(false) }

    //actualiza el state que se usara en el composable principal de esta screen
    if(opcionAceptada) {
        onOpcionSelectedChanged(opcionSeleccionada)
        opcionAceptada = false
    }

    if (showDialog) Desing.MiDialogo(
        show = true,
        titulo = titulo,
        mensaje = mensaje,
        cerrar = { showDialog = false },
        aceptar = {
            opcionAceptada = true
            showDialog = false
        }
    )

    if (showOpciones) {
        val opciones: List<String> = listOf("Unirme", "No Acepto")

        MiDialogoWithOptions2(
            show = true,
            opciones = opciones,
            titulo = titulo,
            mensaje = mensaje,
            cancelar = { showOpciones = false },
            onOptionSelected = {
                opcionSeleccionada = if(it == "Unirme") "S" else  "N"
                opcionAceptada = true
                showOpciones = false
            }
        )
    }
    if (showAviso) Desing.MiAviso(
        show = true,
        titulo = titulo,
        mensaje = mensaje,
        cerrar = { showAviso = false }
    )
    /*****************************************/

    Surface(
        shape = RoundedCornerShape(8.dp),
        elevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 9.dp)
            .clickable {
                if (hojaConParticipantes.hoja.propietaria == "S" ||
                    hojaConParticipantes.participantes.contains(hojaConParticipantes.participantes.find { it.participante.idUsuarioParti == idRegistro })
                )
                    onNavGastos(hojaConParticipantes.hoja.idHoja)
            }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 5.dp)
        ) {
            Icon(
                Icons.Default.Circle,
                contentDescription = "Menu de opciones",
                tint = if(hojaConParticipantes.hoja.propietaria == "N") {
                    if(hojaConParticipantes.participantes.contains(hojaConParticipantes.participantes.find { it.participante.idUsuarioParti == idRegistro })) MaterialTheme.colorScheme.inverseSurface
                    else MaterialTheme.colorScheme.scrim
                }
                else MaterialTheme.colorScheme.tertiaryContainer
            )
            /** IMAGEN Y TITULO **/
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hoja),
                    contentDescription = "Logo Hoja",
                    modifier = Modifier
                        .width(80.dp)
                        .height(80.dp)
                )
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    item{
                        Text(
                            text = hojaConParticipantes.hoja.titulo,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                    if(hojaConParticipantes.hoja.propietaria == "N"){
                        if(!hojaConParticipantes.participantes.contains(hojaConParticipantes.participantes.find { it.participante.idUsuarioParti == idRegistro })){
                            /** INFO DE HOJA NO PROPIETARIA **/
                            item {
                                IconButton(onClick = {
                                    titulo = "INVITACION A ESTA HOJA"
                                    mensaje = "¿Quieres formar parte de esta hoja de gastos?"
                                    onHojaAModificarChanged()
                                    showOpciones = true
                                }) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = "Menu de opciones",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                        else {
                            item {
                                OpcionesHojaNPropi(hojaConParticipantes) { opcion ->
                                    when (opcion) {
                                        "Quitar" -> {
                                            onStatusChanged("Q")
                                            titulo = "QUITAR DE LA LISTA"
                                            mensaje =
                                                "Si acepta, dejará de participar en esta hoja."
                                        }
                                    }
                                    opcionSeleccionada = opcion
                                    showDialog = true
                                }
                            }
                        }
                    }
                    else {
                        /** LISTA DE OPCIONES **/
                        item {
                            OpcionesHoja(hojaConParticipantes) { opcion ->
                                when (opcion) {
                                    "Finalizar" -> {
                                        onStatusChanged("F")
                                        titulo = "FINALIZAR LA HOJA"
                                        mensaje =
                                            "Si acepta, no se podra introducir mas gastos y se debera hacer el balance correspondiente."
                                    }

                                    "Anular" -> {
                                        onStatusChanged("A")
                                        titulo = "ANULAR LA HOJA"
                                        mensaje =
                                            "Si acepta, no se tendra en cuenta ningun gasto y no se realizará ningun balance de los gastos."
                                    }

                                    "Eliminar" -> {
                                        onStatusChanged("E")
                                        titulo = "ELIMINAR LA HOJA"
                                        mensaje =
                                            "Si acepta, se borrará toda la informacion de la hoja y no se podra recuperar."
                                    }
                                }
                                opcionSeleccionada = opcion
                                showDialog = true
                            }
                        }
                    }
                }
            }
            /** DATOS **/
            Datoshojas(hojaConParticipantes, { onNavParticipantes(it) })
        }
    }
}

/** DATOS DE LAS HOJAS **/
@Composable
fun Datoshojas(
    hojaConParticipantes: HojaConParticipantes,
    onNavParticipantes: (Long) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column{
            Text(
                text =  when (hojaConParticipantes.hoja.status) {
                    "C" -> "Activa"
                    "A" -> "Anulada"
                    "B" -> "Balanceada"
                    else ->"Finalizada"
                },
                fontSize = 20.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall
            )
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Text(
                    text = "Participantes:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = hojaConParticipantes.participantes.size.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                Icon(
                    imageVector = Icons.Filled.People,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = "Icono correo a adjuntar",
                    modifier = Modifier
                        .clickable {
                            onNavParticipantes(hojaConParticipantes.hoja.idHoja)
                        }
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
            {
                Text(
                    text = "Fecha Cierre:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = hojaConParticipantes.hoja.fechaCierre ?: "-",
                    style = MaterialTheme.typography.bodyLarge
                )

            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
            {
                Text(
                    text = "Limite:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = if(hojaConParticipantes.hoja.limite.isNullOrEmpty()) "-" else hojaConParticipantes.hoja.limite.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )

            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
            {
                Text(
                    text = "Creada el:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = hojaConParticipantes.hoja.fechaCreacion,
                    style = MaterialTheme.typography.bodyLarge
                )

            }

        }
    }
}

/** LISTA DE OPCIONES DE LAS HOJAS PROPIETARIAS*/
@Composable
fun OpcionesHoja(
    hoja: HojaConParticipantes,
    onOptionSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val statusHoja = hoja.hoja.status

    Column {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreHoriz,
                contentDescription = "Menu de opciones",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if(statusHoja == "C"){
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onOptionSelected("Finalizar")
                    }
                ) {
                    Text("Finalizar")
                }
                DropdownMenuItem(
                    onClick = {
                        expanded = false
                        onOptionSelected("Anular")
                    }
                ) {
                    Text("Anular")
                }
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onOptionSelected("Eliminar")
                }
            ) {
                Text("Eliminar")
            }
        }
    }
}

/** LISTA DE OPCIONES DE LAS HOJAS NO PROPIETARIAS*/
@Composable
fun OpcionesHojaNPropi(
    hoja: HojaConParticipantes,
    onOptionSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val statusHoja = hoja.hoja.status

    Column {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Default.MoreHoriz,
                contentDescription = "Menu de opciones",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    onOptionSelected("Quitar")
                }
            ) {
                Text("Quitar")
            }
        }
    }
}