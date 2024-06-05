package com.app.miscuentas.features.nav_bar_screens.mis_hojas

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.IconButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import com.app.miscuentas.data.local.dbroom.entitys.toDomain
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.util.Desing
import kotlinx.coroutines.flow.MutableStateFlow
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
fun MisHojasScreen(
    innerPadding: PaddingValues?,
    onNavGastos: (Long) -> Unit,
    viewModel: MisHojasViewModel = hiltViewModel()
) {
    val itemsTipo = listOf("Activas", "Finalizadas", "Anuladas", "Todas")
    val itemsOrden = listOf("Fecha creacion", "Fecha cierre")

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
            "Finalizar" -> { viewModel.update() }
            "Anular" -> { viewModel.update() }
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
    }
    else {
        Column(horizontalAlignment = CenterHorizontally) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SpinnerCustoms("Mostrar:", itemsTipo, "Filtrar por tipo"){ mostrar ->
                    when(mostrar) {
                        "Activas" -> {viewModel.onMostrarTipoChanged("C")}
                        "Finalizadas" -> {viewModel.onMostrarTipoChanged("F")}
                        "Anuladas" -> {viewModel.onMostrarTipoChanged("A")}
                        "Todas" -> {viewModel.onMostrarTipoChanged("T")}
                    }

                }
                SpinnerCustoms("Ordenar por:", itemsOrden, "Opcion de ordenacion"){ Orden ->
                     viewModel.onTipoOrdenChanged(Orden)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Descendente",
                    style = MaterialTheme.typography.titleSmall
                )
                Checkbox(
                    checked = hojaState.ordenDesc,
                    onCheckedChange = {
                        viewModel.onOrdenDescChanged(it)

                    }
                )
            }

            LazyColumn(
                contentPadding = innerPadding!!,
            ) {
                if (hojaState.listaHojasAMostrar != null){
                    itemsIndexed(hojaState.listaHojasAMostrar!!){index, hojaConParticipantes ->
                        HojaDesing(
                            onNavGastos = {onNavGastos(it)},
                            hojaConParticipantes = hojaConParticipantes,
                            onOpcionSelectedChanged = { viewModel.onOpcionSelectedChanged(it) },
                            onStatusChanged = {viewModel.onStatusChanged(hojaConParticipantes.hoja.toDomain(), it)}
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

        Text(
            text = titulo,
            style = MaterialTheme.typography.titleSmall
        )

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
 onNavGastos: (Long) -> Unit,
 hojaConParticipantes: HojaConParticipantes,
 onOpcionSelectedChanged: (String) -> Unit,
 onStatusChanged: (String) -> Unit
) {
    var opcionAceptada by rememberSaveable { mutableStateOf(false) }

    /** Eleccion y cambio de estado:  **/
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    var mensaje by rememberSaveable { mutableStateOf("") } //Mensaje a mostrar
    var opcionSeleccionada by rememberSaveable { mutableStateOf("") }

    //actualiza el state que se usara en el composable principal de esta screen
    if(opcionAceptada) {
        onOpcionSelectedChanged(opcionSeleccionada)
        opcionAceptada = false
    }

    if (showDialog) Desing.MiDialogo(
        show = true,
        texto = mensaje,
        cerrar = { showDialog = false },
        aceptar = {
            opcionAceptada = true
            showDialog = false
        }
    )
    /*****************************************/

    Card(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .clickable { onNavGastos(hojaConParticipantes.hoja.idHoja) },
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
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = hojaConParticipantes.hoja.titulo,
                    style = MaterialTheme.typography.titleLarge
                )

                //Spacer(modifier = Modifier.weight(1f))
                /** API **/ //   Text(text = hoja.type)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {


                    when (hojaConParticipantes.hoja.status) { //pinta segun valor status de la BBDD
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
                    OpcionesHoja { opcion ->
                        when(opcion) {
                            "Resumen" ->  mensaje = "Este es el resumen"

                            "Finalizar" ->  {
                                onStatusChanged("F")
                                mensaje = "Finalizar la hoja"
                            }

                            "Anular" ->  {
                                onStatusChanged("A")
                                mensaje = "Esta hoja se marcara como anulada."
                            }
                            "Eliminar" ->  {
                                onStatusChanged("E")
                                mensaje = "¿Seguro que desea ELIMINAR esta hoja?"
                            }
                        }
                        opcionSeleccionada = opcion
                        showDialog = true
                    }
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
                        Text(
                            text = "Participantes:",
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = hojaConParticipantes.participantes.size.toString(),
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
                            text = hojaConParticipantes.hoja.fechaCierre ?: "sin definir",
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
                            text = if(hojaConParticipantes.hoja.limite == null) "sin definir" else hojaConParticipantes.hoja.limite.toString(),
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
                            text = hojaConParticipantes.hoja.fechaCreacion!!,
                            style = MaterialTheme.typography.titleMedium
                        )
                        /** API **/ //   Text(text = hoja.type)
                    }

                }
            }
        }
    }
}


/** Composable para las opciones de la hoja **/
@Composable
fun OpcionesHoja(
    onOptionSelected: (String) -> Unit
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box {
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
            DropdownMenuItem(onClick = {
                expanded = false
                onOptionSelected("Resumen")
            }) {
                Text("Resumen")
            }
            DropdownMenuItem(onClick = {
                expanded = false
                onOptionSelected("Finalizar")
            }) {
                Text("Finalizar")
            }
            DropdownMenuItem(onClick = {
                expanded = false
                onOptionSelected("Anular")
            }) {
                Text("Anular")
            }
            DropdownMenuItem(onClick = {
                expanded = false
                onOptionSelected("Eliminar")
            }) {
                Text("Eliminar")
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