package com.app.miscuentas.features.inicio

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Output
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.miscuentas.R
import com.app.miscuentas.data.domain.SessionManager
import com.app.miscuentas.data.local.dbroom.relaciones.HojaConParticipantes
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.util.Desing
import com.app.miscuentas.util.Desing.Companion.MiAviso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
//@Preview
//@Composable
//fun Prev(){
//    val navController = rememberNavController()
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentScreen = backStackEntry?.destination?.route ?: MisCuentasScreen.MisHojas.route
//
//    val onNavSplash: () -> Unit = {}
//    val onNavMisHojas: () -> Unit = {}
//    val onNavNuevaHoja: () -> Unit = {}
//
//    Inicio(
//        currentScreen,
//        {navController.navigateUp()},
//        onNavSplash = onNavSplash,
//        onNavMisHojas = onNavMisHojas,
//        onNavNuevaHoja = onNavNuevaHoja
//    )
//}

/** ESTRUCTURA DE VISTA CON SCAFFOLD **/
@SuppressLint("CoroutineCreationDuringComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Inicio(
    onNavSplash: () -> Unit,
    onNavMisHojas: () -> Unit,
    onNavNuevaHoja: () -> Unit,
    onNavNuevoGasto: (Long) -> Unit,
    viewModel: InicioViewModel = hiltViewModel()
){

    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val inicioState by viewModel.inicioState.collectAsState()
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())


    LaunchedEffect(Unit) {
        viewModel.getIdHojaPrincipalPreference()
        viewModel.getAllHojasCalculos()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            MyDrawer(
                context,
                { viewModel.onInicioHuellaChanged(it) },
                viewModel::cerrarSesion,
                inicioState.registrado,
                inicioState.huellaDigital,
                onNavSplash
            )
        }
    ) {

        Scaffold(
            scaffoldState = scaffoldState,
            modifier = Modifier.fillMaxSize(),
            topBar = {
                MiTopBar(
                    title = "Inicio",
                    canNavigateBack = false,
                    navigateUp = {},
                    drawerState = drawerState,
                    scope = scope
                    //scrollBehavior = scrollBehavior
                )
            },
            content = { innerPadding ->
                InicioContent(
                    innerPadding,
                    onNavMisHojas,
                    onNavNuevaHoja,
                    onNavNuevoGasto,
                    inicioState.hojaPrincipal,
                    inicioState.idHojaPrincipal,
                    inicioState.totalHojas
                )
            }
        )
    }
}


/** CONTENIDO GENERAL DE ESTA SCREEN **/
@Composable
fun InicioContent(
    innerPadding: PaddingValues,
    onNavMisHojas: () -> Unit,
    onNavNuevaHoja: () -> Unit,
    onNavNuevoGasto: (Long) -> Unit,
    hojaPrincipal: HojaConParticipantes?,
    idHojaPrincipal: Long,
    totalHojas: Int
) {

    var showDialog by remember { mutableStateOf(false) } //valor mutable para el dialogo
    var mensaje by remember { mutableStateOf("")}

    if (showDialog) MiAviso(true, titulo = "IMPORTANTE", mensaje, {showDialog = false})

    val existeHoja = {
        when {
            totalHojas >  0 ->  onNavMisHojas()
            else -> {
                mensaje = "Aun no has creado ninguna hoja."
                showDialog = true
            }
        }
    }

    val accesoRapido = {
        when {
            idHojaPrincipal.toInt() != 0 ->  onNavNuevoGasto(hojaPrincipal!!.hoja.idHoja)
            else -> {
                mensaje = "Selecciona una hoja activa y se guardará en el acceso rapido."
                showDialog = true
            }
        }
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .background(Color(color = 0xFFF5EFEF))

    ) {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(26.dp)
        ) {
            val robotoItalic = FontFamily(Font(R.font.roboto_bolditalic))
            item {
                CustomSpacer(20.dp)

                Card(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .height(IntrinsicSize.Min)
                        .clickable { existeHoja() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiaryContainer)
                            .size(330.dp, 150.dp)
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ver...",
                            modifier = Modifier
                                .align(Alignment.Bottom),
                            fontSize = 30.sp,
                            fontFamily = robotoItalic
                        )
                        ImagenCustom(R.drawable.mis_hojas, "Boton de Mis_Hojas")
                    }
                }
                Card(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .height(IntrinsicSize.Min)
                        .clickable { onNavNuevaHoja() },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Black
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.outlineVariant)
                            .size(330.dp, 150.dp)
                            .padding(start = 20.dp, top = 20.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Crear...",
                            modifier = Modifier
                                .align(Alignment.Bottom),
                            fontSize = 30.sp,
                            fontFamily = robotoItalic
                        )
                        ImagenCustom(R.drawable.nueva_hoja, "Boton de Nueva_Hoja")
                    }
                }
                CustomSpacer(60.dp)

                Text(
                    text = "Acceso rapido",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = "Hoja: " + (hojaPrincipal?.hoja?.titulo ?: "selecciona una"),
                    style = MaterialTheme.typography.titleSmall
                )
                Box(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.large)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.nuevo_gasto),
                        contentDescription = "Boton de nuevo gasto",
                        modifier = Modifier
                            .width(67.dp)
                            .height(67.dp)
                            .clickable { accesoRapido() }
                            .fillMaxSize()
                    )
                }

            }
        }
    }
}

/** ESPACIADOR **/
@Composable
fun CustomSpacer(padding: Dp) {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(padding)
    )
}


/** COMPONENTE IMAGEN **/
@Composable
fun ImagenCustom(
    imagen: Int,
    descript: String
){
    Image(
        painter = painterResource(id = imagen),
        contentDescription = descript,
        modifier = Modifier
            .width(180.dp)
            .height(180.dp)
    )
}


/** MENU LATERAL **/
@Composable
fun MyDrawer(
    context: Context,
    onInicioHuellaChanged: (Boolean) -> Unit,
    cerrarSesion: () -> Unit,
    registradoState: String,
    inicioState: Boolean,
    onNavSplash: () -> Unit
)
{
    //Para cierre de sesion y de app
    val activity = (LocalContext.current as? Activity)
    val miCoroutine = CoroutineScope(Dispatchers.Main)

    //Aviso de la huella digital
    var showAviso by rememberSaveable { mutableStateOf(false) }
    if (showAviso) MiAviso(showAviso, titulo = "IMPORTANTE", "Proximo a gestionar") { showAviso = false }

    ModalDrawerSheet(
        drawerShape = MaterialTheme.shapes.extraLarge,
        drawerTonalElevation = 10.dp
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo), // Reemplaza con tu recurso de icono de la aplicación
                contentDescription = "Mis Cuentas",
                modifier = Modifier.size(88.dp)
            )
            Text(
                registradoState,
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        LazyColumn{

            item {
                HorizontalDivider()
                Text(
                    "SEGURIDAD",
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    NavigationDrawerItem(
                        label = {
                            Text(
                                style = MaterialTheme.typography.labelLarge,
                                text = "Inicio con huella"
                            )
                                },
                        selected = false,
                        onClick = { showAviso = true },
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Fingerprint,
                                contentDescription = "Inicio con huella"
                            )
                        },
                        badge = {
                            Switch(
                                checked = inicioState,
                                onCheckedChange = {onInicioHuellaChanged(it)}
                            )
                        }
                    )
                }
                HorizontalDivider()
                Text(
                    "OPCIONAL",
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Calificar la APP")
                            },
                    selected = false,
                    onClick = { Desing.calificarAPP(context) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Calificar"
                        )
                    }
                )
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Contactar") },
                    selected = false,
                    onClick = { Desing.envioCorreo(context) },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Mail,
                            contentDescription = "Contactar"
                        )
                    }
                )
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Compartir APP")
                            },
                    selected = false,
                    onClick = {
                        // Texto a enviar
                        val ruta =
                            "https://play.google.com/store/apps/details?id=com.bandainamcoent.dblegends_ww&gl=ES" //OBVIAMENTE AUN NO TENGO RUTA DE ESTA APP EN LA TIENDA!!
                        val mensajeYRuta =
                            "Prueba la APP para realizar las cuentas de una manera simple, ya sea con la familia, pareja o amigos.\n$ruta"
                        Desing.compartirAPP(context, mensajeYRuta)
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Compartir APP"
                        )
                    }
                )
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Donar (pendiente)") },
                    selected = false,
                    onClick = {  Toast.makeText(context, "Pendiente de configurar", Toast.LENGTH_SHORT).show() },//PENDIENTE
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "Donar"
                        )
                    }
                )
                HorizontalDivider()

                Text(
                    "DUDAS",
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "FAQ (pendiente)") },
                    selected = false,
                    onClick = {  Toast.makeText(context, "Pendiente de configurar", Toast.LENGTH_SHORT).show() },//PENDIENTE
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.QuestionMark,
                            contentDescription = "FAQ"
                        )
                    }
                )
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Politicas de privacidad (pendiente)") },
                    selected = false,
                    onClick = {  Toast.makeText(context, "Pendiente de configurar", Toast.LENGTH_SHORT).show() },//PENDIENTE
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = "Politicas"
                        )
                    }
                )
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Terminos y condiciones (pendiente)") },
                    selected = false,
                    onClick = {  Toast.makeText(context, "Pendiente de configurar", Toast.LENGTH_SHORT).show() },//PENDIENTE
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = "Condiciones"
                        )
                    }
                )
                HorizontalDivider()

                Text(
                    "TERMINAR",
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                /** Boton SALIR **/
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Salir") },
                    selected = false,
                    onClick = {
                        // salimos de la app
                        activity?.finish()
                    },
                    icon = { Icon(imageVector = Icons.Filled.Output, contentDescription = "Salir") }
                )
                /** Boton CERRAR SESION **/
                NavigationDrawerItem(
                    label = { Text(
                        style = MaterialTheme.typography.labelLarge,
                        text = "Cerrar sesion") },
                    selected = false,
                    onClick = {
                        cerrarSesion()
                        miCoroutine.launch {
                            delay(500)
                            onNavSplash()
                        }
                    },
                    icon = { Icon(imageVector = Icons.Filled.Close, contentDescription = "Cerrar") }
                )
            }
        }
    }
}
