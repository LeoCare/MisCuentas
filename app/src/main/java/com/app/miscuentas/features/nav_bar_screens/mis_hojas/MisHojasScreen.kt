package com.app.miscuentas.features.nav_bar_screens.mis_hojas

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
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
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
import com.app.miscuentas.domain.model.HojaCalculo
import com.app.miscuentas.util.Desing


/** Contenedor del resto de elementos para la pestaña Hojas **/
@Composable
fun MisHojasScreen(
    innerPadding: PaddingValues?,
    onNavGastos: (Long) -> Unit,
    viewModel: MisHojasViewModel = hiltViewModel()
) {

    val hojaState by viewModel.misHojasState.collectAsState()

    //obtiene id del registrado para obtener sus hojas (ver siguiente Launched)
    LaunchedEffect(Unit) {
        viewModel.getIdRegistroPreference()
    }

    //obtiene las hojas del resitrado
    LaunchedEffect(hojaState.idRegistro){
        viewModel.getAllHojaConParticipantes()
    }

    LaunchedEffect(hojaState.opcionSelected){
        when(hojaState.opcionSelected) {
            "Finalizar","Anular" -> { viewModel.updateStatusHoja() }
            "Eliminar" -> { viewModel.deleteHojaConParticipantes() }
        }
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
    } else {
        Column(
            modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues())
        ) {
            SeleccionFiltros(
                filtroElegido = hojaState.filtroElegido,
                ordenElegido = hojaState.ordenElegido,
                descending = hojaState.descending,
                onMostrarFiltroChanged = viewModel::onMostrarFiltroChanged,
                onTipoOrdenChanged = viewModel::onTipoOrdenChanged,
                onDescendingChanged = viewModel::onDescendingChanged
            )
            Spacer(modifier = Modifier.height(8.dp))
            HojasList(
                innerPadding,
                hojaState.listaHojasAMostrar,
                onNavGastos,
                viewModel::onOpcionSelectedChanged,
                viewModel::onStatusChanged
            )
        }
    }
}


@Composable
fun SeleccionFiltros(
    filtroElegido: String,
    ordenElegido: String,
    descending: Boolean,
    onMostrarFiltroChanged: (String) -> Unit,
    onTipoOrdenChanged: (String) -> Unit,
    onDescendingChanged: (Boolean) -> Unit
) {

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
                onClick = { onMostrarFiltroChanged("C") },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filtroElegido == "C") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(if (filtroElegido == "C") 37.dp else 32.dp)
            ){
                Text(
                    text = "Activ.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (filtroElegido == "C") Color.White else Color.Black
                )
            }
            Button(
                onClick = { onMostrarFiltroChanged("F") },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filtroElegido == "F") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(if (filtroElegido == "F") 37.dp else 32.dp)
            ){
                Text(
                    text = "Finali.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (filtroElegido == "F") Color.White else Color.Black
                )
            }
            Button(
                onClick = { onMostrarFiltroChanged("A") },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filtroElegido == "A") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(if (filtroElegido == "A") 37.dp else 32.dp)
            ){
                Text(
                    text = "Anul.",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (filtroElegido == "A") Color.White else Color.Black
                )
            }
            Button(
                onClick = { onMostrarFiltroChanged("T") },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (filtroElegido == "T") MaterialTheme.colorScheme.primary else Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(if (filtroElegido == "T") 37.dp else 32.dp)
            ){
                Text(
                    text = "Todos",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (filtroElegido == "T") Color.White else Color.Black
                )
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
                style = MaterialTheme.typography.bodyMedium
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
            .background(Color.LightGray),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Text(
            style = MaterialTheme.typography.titleMedium,
            text = "Mostrar  ${
                when (filtroElegido) {
                    "C" -> "Activas"
                    "F" -> "Finalizadas"
                    "B" -> "Balanceadas"
                    "A" -> "Anuladas"
                    else  -> "Todos"
                }
            }"

        )
    }
}

@Composable
fun HojasList(
    innerPadding: PaddingValues?,
    listaHojasAMostrar: List<HojaConParticipantes>?,
    onNavGastos: (Long) -> Unit,
    onOpcionSelectedChanged: (String) -> Unit,
    onStatusChanged: (HojaCalculo, String) -> Unit
) {
    LazyColumn(contentPadding = innerPadding!!) {
        listaHojasAMostrar?.let { list ->
            itemsIndexed(list) { _, hojaConParticipantes ->
                HojaDesing(
                    onNavGastos = { onNavGastos(it) },
                    hojaConParticipantes = hojaConParticipantes,
                    onOpcionSelectedChanged = { onOpcionSelectedChanged(it) },
                    onStatusChanged = { onStatusChanged(hojaConParticipantes.hoja.toDomain(), it) }
                )
            }
        }
    }
}


@Composable
fun HojaDesing(
 /** API **/ //  hoja: Hoja
 onNavGastos: (Long) -> Unit,
 hojaConParticipantes: HojaConParticipantes,
 onOpcionSelectedChanged: (String) -> Unit,
 onStatusChanged: (String) -> Unit
) {

    /** Eleccion y cambio de estado:  **/
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    var titulo by rememberSaveable { mutableStateOf("") } //Titulo a mostrar
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar
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
    /*****************************************/

    Surface(
        shape = RoundedCornerShape(8.dp),
        elevation = 12.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 5.dp)
            .clickable { onNavGastos(hojaConParticipantes.hoja.idHoja) },
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
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
                        /** LISTA DE OPCIONES **/

                    }
                    /** LISTA DE OPCIONES **/
                    item{
                        OpcionesHoja(hojaConParticipantes) { opcion ->
                            when(opcion) {
                                "Finalizar" ->  {
                                    onStatusChanged("F")
                                    titulo = "FINALIZAR LA HOJA"
                                    mensaje = "Si acepta, no se podra introducir mas gastos y se debera hacer el balance correspondiente."
                                }

                                "Anular" ->  {
                                    onStatusChanged("A")
                                    titulo = "ANULAR LA HOJA"
                                    mensaje = "Si acepta, no se tendra en cuenta ningun gasto y no se realizará ningun balance de los gastos."
                                }
                                "Eliminar" ->  {
                                    onStatusChanged("E")
                                    titulo = "ELIMINAR LA HOJA"
                                    mensaje = "Si acepta, se borrará toda la informacion de la hoja y no se podra recuperar."
                                }
                            }
                            opcionSeleccionada = opcion
                            showDialog = true
                        }
                    }
                }
                /** API **/  //   Text(text = hoja.type)


            }
            /** DATOS **/
            Datoshojas(hojaConParticipantes)
        }
    }
}

/** DATOS DE LAS HOJAS **/
@Composable
fun Datoshojas(
    hojaConParticipantes: HojaConParticipantes,
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
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
            {
                Text(
                    text = "Participantes:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = hojaConParticipantes.participantes.size.toString(),
                    style = MaterialTheme.typography.bodyLarge
                )
                /** API **/
                /** API **/  //   Text(text = hoja.price.toString())
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
                /** API **/
                /** API **/  //  Text(text = hoja.id)
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
                /** API **/
                /** API **/  //  Text(text = hoja.id)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp))
            {
                Text(
                    text = "Creada el:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = hojaConParticipantes.hoja.fechaCreacion!!,
                    style = MaterialTheme.typography.bodyLarge
                )
                /** API **/
                /** API **/  //   Text(text = hoja.type)
            }

        }
    }
}

/** LISTA DE OPCIONES DE LAS HOJAS **/
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