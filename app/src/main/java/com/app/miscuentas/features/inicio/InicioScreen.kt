package com.app.miscuentas.features.inicio

import android.Manifest
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.Icon
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.navigation.NavHostController
import com.app.miscuentas.R
import com.app.miscuentas.features.navegacion.MiTopBar
import com.app.miscuentas.features.navegacion.MisCuentasScreen
import com.app.miscuentas.util.Desing
import com.app.miscuentas.util.MiAviso
import com.app.miscuentas.util.Permiso
import com.app.miscuentas.util.PermisoState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

//BORRAR ESTO, SOLO ES PARA PREVISUALIZAR
//@Preview
//@Composable
//fun Prev(){
//    val navController = rememberNavController()
//    val backStackEntry by navController.currentBackStackEntryAsState()
//    val currentScreen = MisCuentasScreen.valueOf(
//        backStackEntry?.destination?.route ?: MisCuentasScreen.Inicio.name
//    )
//
//    Inicio(
//        currentScreen,
//        navController,
//        statePermisoCamara,
//        onNavSplash = { navController.navigate(MisCuentasScreen.Splash.name) },
//        onNavMisHojas = { navController.navigate(MisCuentasScreen.MisHojas.name) }
//    ) { navController.navigate(MisCuentasScreen.NuevaHoja.name) }
//}

/** ESTRUCTURA DE VISTA CON SCAFFOLD **/
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun Inicio(
    currentScreen: MisCuentasScreen,
    navController: NavHostController,
    onNavSplash: () -> Unit,
    onNavMisHojas: () -> Unit,
    onNavNuevaHoja: () -> Unit
){
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val viewModel: InicioViewModel = hiltViewModel()
    val inicioState by viewModel.inicioState.collectAsState()
    /** Permisos  **/
    val statePermisoCamara1 = rememberPermissionState(permission = Manifest.permission.CAMERA)
    val permiso = Permiso()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { MyDrawer(context, viewModel, inicioState, onNavSplash) }
    ) {
        Scaffold( //La funcion Scaffold tiene la estructura para crear una view con barra de navegacion
            scaffoldState = scaffoldState,
            topBar = {
                MiTopBar(
                    context,
                    drawerState,
                    currentScreen,
                    scope = scope,
                    scaffoldState = scaffoldState,
                    canNavigateBack = false,
                    navigateUp = { navController.navigateUp() }
                ) { permiso.solicitarPermiso(statePermisoCamara1) }
            },
            content = { innerPadding -> InicioContent(context, permiso, statePermisoCamara1, innerPadding, onNavMisHojas, onNavNuevaHoja) }
        )
    }
}



/** CONTENIDO GENERAL DE ESTA SCREEN **/
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InicioContent(
    context: Context,
    permiso: Permiso,
    statePermisoCamara1: PermissionState,
    innerPadding: PaddingValues,
    onNavMisHojas: () -> Unit,
    onNavNuevaHoja: () -> Unit
) {

    /** Permisos  **/
//    val statePermisoCamara1 = rememberPermissionState(permission = Manifest.permission.CAMERA)
//    val permiso = Permiso()
    val permisoState by permiso.permisoState.collectAsState()

    //Este aviso se lanzara cuando se deniega el permiso...
    var showDialog by rememberSaveable { mutableStateOf(false) } //valor mutable para el dialogo
    if (showDialog) MiAviso(
        show = true,
        texto = "El permiso es necesaro para enviar una captura. Si se deniega una vez mas solo se podra otorgar desde la configuracion del dispositivo."
    )
    { showDialog = false }

    //Comprobacion del permiso solicitado
//    LaunchedEffect(permisoState.permisoState) {

    if (statePermisoCamara1.status.isGranted)
        permiso.setPermisoConcedido()
    else if (statePermisoCamara1.status.shouldShowRationale)
        permiso.setPermisoDenegPermanente()
    else
        permiso.setPermisoDenegado()
//    }

    //Accion despues de la comprobacion
    LaunchedEffect(permisoState.permisoState){
        when(permisoState.permisoState){
            is PermisoState.PermissionState.Concedido -> {
                Toast.makeText(context, "Permiso concedido", Toast.LENGTH_SHORT).show()
            }
            is  PermisoState.PermissionState.DenegPermanente -> { showDialog = true }
            else -> {}
        }
    }

    /** CAJA CON COLUMNAS: **/
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize() //estira al maximo
            .background(Color(color = 0xFFF5EFEF))

    ) {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally, // Alinear horizontalmente en la columna
            verticalArrangement = Arrangement.spacedBy(26.dp), // Espacio entre elementos de la columna
        ) {
            val robotoItalic = FontFamily(Font(R.font.roboto_bolditalic))

            item {
                CustomSpacer(40.dp)

                Card(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .clip(MaterialTheme.shapes.extraLarge)
                        .height(IntrinsicSize.Min)
                        .clickable { onNavMisHojas() },
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
                CustomSpacer (30.dp)
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
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun MyDrawer(
    context: Context,
    viewModel: InicioViewModel,
    inicioState: InicoState,
    onNavSplash: () -> Unit
)
{
    //Para cierre de sesion y de app
    val activity = (LocalContext.current as? Activity)
    val miCoroutine = CoroutineScope(Dispatchers.Main)

    //Aviso de la huella digital
    var showAviso by rememberSaveable { mutableStateOf(false) }
    if (showAviso) MiAviso(showAviso, "Proximo a gestionar") { showAviso = false }

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
                "Bienvenido",
                modifier = Modifier.padding(10.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        LazyColumn{

            item {
                Divider()
                Text(
                    "OPCIONAL",
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Row {
                    NavigationDrawerItem(
                        label = {
                            Text(text = "Inicio con huella")
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
                                checked = inicioState.huellaDigital,
                                onCheckedChange = { viewModel.onInicioHuellaChanged(it) }
                            )
                        }
                    )
                }
                Divider()
                Text(
                    "AYUDA",
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text(text = "Calificar la APP") },
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
                    label = { Text(text = "Contactar") },
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
                    label = { Text(text = "Donar (pendiente)") },
                    selected = false,
                    onClick = {  Toast.makeText(context, "Pendiente de configurar", Toast.LENGTH_SHORT).show() },//PENDIENTE
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.CardGiftcard,
                            contentDescription = "Donar"
                        )
                    }
                )
                Divider()

                Text(
                    "DUDAS",
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text(text = "Info (pendiente)") },
                    selected = false,
                    onClick = {  Toast.makeText(context, "Pendiente de configurar", Toast.LENGTH_SHORT).show() },//PENDIENTE
                    icon = { Icon(imageVector = Icons.Filled.Info, contentDescription = "Info") }
                )
                NavigationDrawerItem(
                    label = { Text(text = "FAQ (pendiente)") },
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
                    label = { Text(text = "Politicas de privacidad (pendiente)") },
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
                    label = { Text(text = "Terminos y condiciones (pendiente)") },
                    selected = false,
                    onClick = {  Toast.makeText(context, "Pendiente de configurar", Toast.LENGTH_SHORT).show() },//PENDIENTE
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = "Condiciones"
                        )
                    }
                )
                Divider()

                Text(
                    "TERMINAR",
                    modifier = Modifier.padding(start = 16.dp, top = 10.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                NavigationDrawerItem(
                    label = { Text(text = "Salir") },
                    selected = false,
                    onClick = {
                        // salimos de la app
                        activity?.finish()
                    },
                    icon = { Icon(imageVector = Icons.Filled.Output, contentDescription = "Salir") }
                )
                NavigationDrawerItem(
                    label = { Text(text = "Cerrar sesion") },
                    selected = false,
                    onClick = {
                        viewModel.onInicioHuellaChanged(false)
                        viewModel.onRegistroPreferenceChanged(false)
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